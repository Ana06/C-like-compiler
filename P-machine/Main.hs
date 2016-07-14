{-# LANGUAGE DeriveDataTypeable #-}

module Main (main) where

-----------------------------------------------------------------------------
-- |
-- Module      :  Main
-- Copyright   :  (c) Ricardo Peña, Jun. 2014, Universidad Complutense de Madrid
--                P-machine emulator
-- License     :  LGPL
--
-- Maintainer  :  ricardo@sip.ucm.es
-- Stability   :  provisional
-- Portability :  portable
--
--  It provides a complete interpreter of the original N. Wirth's P-machine
--
--     . It expects a text file having a sequence of P-instructions ended by a ';'
--     . It sets up a CODE segment and a STORE array, and executes the code
--     . As a result, the final machine state is printed
--     . A set of exceptions are checked. If any of them arises, the machine
--       reports the error, and stops
--
-----------------------------------------------------------------------------
--
import System.Environment
import qualified Data.List as L
import qualified System.Exit as S
import qualified Data.Typeable as T
import qualified Data.Map as M
import qualified Control.Exception as E

import Lexer
import Parser


-----------------------------------------------------------------------------
-- * Data types
--
-- | They define the basic P-machine types: values, instructions, the code store,
--   the data store, and the machine internal state
--
-----------------------------------------------------------------------------

-- Values and instructions

   -- dataypes Val and Instr are defined in the Parser module

   -- functions to inspect the type of a value
int :: Val -> Bool
int (Int _) = True
int _       = False

bool :: Val -> Bool
bool (Bool _) = True
bool  _       = False

-- The code store
type CODE = M.Map Int Instr

-- The data store
type STORE = M.Map Int Val

-- The machine registers
type PC = Int
type SP = Int
type MP = Int
type EP = Int
type NP = Int

-- The machine internal state. The last Int is the code size
newtype STATE = St (PC, MP, SP, EP, NP, STORE, Int)
        
instance Show STATE where
      showsPrec p (St (pc,mp,sp,ep,np,store,cdSize)) =
                showString("Final P-machine state:") .
                showString ("\nPC = ") .
                shows pc .
                showString ("   MP = ") .
                shows mp .
                showString ("   SP = ") .
                shows sp .
                showString ("   EP = ") .
                shows ep .
                showString ("   NP = ") .
                shows np .
                showString ("   code size = ") .
                shows cdSize .
                showString ("\nSTORE = \n") .
                (showSTORE store) 

showSTORE st = showString $ L.intercalate "\n" (map show s)
  where s = M.toList st


-- The default size of the data store (If you need more memory, change it)
stSize = 100


-- The initial P-machine state. All memory positions are set to Null
initialState :: Int -> STATE
initialState cdSize = St (0, 0, -1, -1, stSize, 
                          M.fromList (zip [0..stSize-1] (repeat Null)),cdSize)


-----------------------------------------------------------------------------
-- * Interpreter
--
-- | The P-machine complete operation is as follows:
--
--    1. It reads and parses the input file
--    2. Should it not arise any errors, it enters the main interpretation loop
--    3. The machine, 
--             (a) may stop normally in a STP instruction, or
--             (b) may stop abnormally because of an exception, or 
--             (c) it may run forever
--    4. If it stops, the final state is printed.
--
-----------------------------------------------------------------------------

-- The P-machine main program
main = do a <- getArgs             -- the input file name is a program argument
          s <- readFile (head a)
          let code = M.fromList . zip [0..] . parser . lexer $ s 
          execute code          

-- The main interpretation loop

execute :: CODE -> IO ()
execute code = E.catch                  -- the main loop. It may raise exceptions
               (loop code (initialState $ M.size code)) 
               dealWithExceptions       -- the exception handler

loop :: CODE -> STATE -> IO ()
loop code st@(St (pc,mp,sp,ep,np,store,cds)) = 
     if instr == STP 
        then do putStrLn "Run successfully completed"
                print st
        else do let newSt = interpret instr $ St (pc+1,mp,sp,ep,np,store,cds)
                loop code newSt
     where instr = code M.! pc


-- The set of exceptions

data PmachineException  = LabelOutOfRange Instr STATE
                        | AdressOutOfRange Instr STATE
                        | AddressNotInt Instr STATE
                        | NonArithmeticValue Instr STATE
                        | NonBooleanValue Instr STATE
                        | StackHeapCollision Instr STATE
                        | IndexOutOfRange Instr STATE
     deriving (Show,T.Typeable)

instance E.Exception PmachineException


-- The exception handler just prints the details

dealWithExceptions :: PmachineException -> IO ()

dealWithExceptions (LabelOutOfRange i st) = 
           do putStrLn ("Code address out of range in instruction " ++ show i)
              print st
              S.exitFailure

dealWithExceptions (AdressOutOfRange i st) = 
           do putStrLn ("Store address out of range in instruction " ++ show i)
              print st
              S.exitFailure

dealWithExceptions (AddressNotInt i st) = 
           do putStrLn ("Store address is not an integer in instruction " ++ show i)
              print st
              S.exitFailure

dealWithExceptions (NonArithmeticValue i st) = 
           do putStrLn ("Non arithmetic value in instruction " ++ show i)
              print st
              S.exitFailure

dealWithExceptions (NonBooleanValue i st) = 
           do putStrLn ("Non boolean value in instruction " ++ show i)
              print st
              S.exitFailure

dealWithExceptions (StackHeapCollision i st) = 
           do putStrLn ("Stack and heap collision in instruction " ++ show i)
              print st
              S.exitFailure

dealWithExceptions (IndexOutOfRange i st) = 
           do putStrLn ("Array index out of range in instruction " ++ show i)
              print st
              S.exitFailure


     
--------------------------------------------------------------------------------
-- * Interpretation of every single instruction
--
-- | Each instruction may modify the current state and may raise an exception
--
--------------------------------------------------------------------------------


-- Auxiliary function for traversing the stack

base :: Instr -> STATE -> Int -> Int -> Int

base _  _                           0 mp        = mp
base i  st@(St (_,_,_,_,_,store,_)) n mp | n> 0 = 
        if not (int v) 
           then E.throw (AddressNotInt i st)
           else base i st (n-1) mp'
        where v@ ~(Int mp') = store M.! (mp + 1)

--
-- The interpreter
--

interpret :: Instr -> STATE -> STATE


-- Load and store group

interpret i@(LDO q) st@(St (pc,mp,sp,ep,np,store,cds)) =
          if q > sp || q >= stSize
             then E.throw (AdressOutOfRange i st) 
             else St (pc,mp,sp+1,ep,np,M.insert (sp+1) (store M.! q) store,cds) 

interpret (LDC v) (St (pc,mp,sp,ep,np,store,cds)) =
                   St (pc,mp,sp+1,ep,np,M.insert (sp+1) v store,cds)

interpret i@IND  st@(St (pc,mp,sp,ep,np,store,cds)) =
          if not (int v) 
             then E.throw (AddressNotInt i st)
             else if orig > sp && orig < np
                  then E.throw (AdressOutOfRange i st) 
                  else St (pc,mp,sp,ep,np,M.insert sp (store M.! orig) store,cds)
          where v@ ~(Int orig) = store M.! sp         

interpret i@(SRO q) st@(St (pc,mp,sp,ep,np,store,cds)) =
          if q > sp || q >= stSize
             then E.throw (AdressOutOfRange i st) 
             else St (pc,mp,sp-1,ep,np,M.insert q (store M.! sp) store,cds)

interpret i@STO st@(St (pc,mp,sp,ep,np,store,cds)) =
          if not (int v)
             then E.throw (AddressNotInt i st)
             else if q > sp && q < np
                  then E.throw (AdressOutOfRange i st) 
                  else St (pc,mp,sp-2,ep,np,M.insert q (store M.! sp) store,cds)
          where v@ ~(Int q) = store M.! (sp-1)

interpret i@(LOD p q) st@(St (pc,mp,sp,ep,np,store,cds)) =
          if var > sp || var >= stSize
             then E.throw (AdressOutOfRange i st) 
             else St (pc,mp,sp+1,ep,np,M.insert (sp+1) (store M.! var) store,cds) 
          where var = (base i st p mp) + q

interpret i@(LDA p q) st@(St (pc,mp,sp,ep,np,store,cds)) = 
                          St (pc,mp,sp+1,ep,np,M.insert (sp+1) (Int addr) store,cds) 
          where addr = (base i st p mp) + q

interpret i@(STR p q) st@(St (pc,mp,sp,ep,np,store,cds)) =
          if var > sp || var >= stSize
             then E.throw (AdressOutOfRange i st) 
             else St (pc,mp,sp-1,ep,np,M.insert var (store M.! sp) store,cds)
          where var = (base i st p mp) + q


-- Arithmetic and logical group

interpret i@ADD  st@(St (pc,mp,sp,ep,np,store,cds)) =
          if not (int v1) || not (int v2) 
             then E.throw (NonArithmeticValue i st)
             else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Int (a1+a2)) store,cds)
          where v1@ ~(Int a1) = store M.! (sp-1) 
                v2@ ~(Int a2) = store M.! sp

interpret i@SUB  st@(St (pc,mp,sp,ep,np,store,cds)) =
          if not (int v1) || not (int v2) 
             then E.throw (NonArithmeticValue i st)
             else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Int (a1-a2)) store,cds)
          where v1@ ~(Int a1) = store M.! (sp-1) 
                v2@ ~(Int a2) = store M.! sp

interpret i@MUL  st@(St (pc,mp,sp,ep,np,store,cds)) =
          if not (int v1) || not (int v2) 
             then E.throw (NonArithmeticValue i st)
             else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Int (a1*a2)) store,cds)
          where v1@ ~(Int a1) = store M.! (sp-1) 
                v2@ ~(Int a2) = store M.! sp

interpret i@DIV  st@(St (pc,mp,sp,ep,np,store,cds)) =
    if not (int v1) || not (int v2) 
       then E.throw (NonArithmeticValue i st)
       else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Int (a1 `div` a2)) store,cds)
       where v1@ ~(Int a1) = store M.! (sp-1) 
             v2@ ~(Int a2) = store M.! sp

interpret i@NEG  st@(St (pc,mp,sp,ep,np,store,cds)) =
          if not (int v)  
             then E.throw (NonArithmeticValue i st)
             else St (pc,mp,sp,ep,np,M.insert sp (Int (-a)) store,cds)
          where v@ ~(Int a) = store M.! sp

interpret i@AND  st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (bool v1) || not (bool v2) 
          then E.throw (NonBooleanValue i st)
          else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (b1 && b2)) store,cds)
          where v1@ ~(Bool b1) = store M.! (sp-1) 
                v2@ ~(Bool b2) = store M.! sp

interpret i@OR  st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (bool v1) || not (bool v2) 
          then E.throw (NonBooleanValue i st)
          else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (b1 || b2)) store,cds)
          where v1@ ~(Bool b1) = store M.! (sp-1) 
                v2@ ~(Bool b2) = store M.! sp

interpret i@NOT  st@(St (pc,mp,sp,ep,np,store,cds)) =
          if not (bool v) 
             then E.throw (NonBooleanValue i st)
             else St (pc,mp,sp,ep,np,M.insert (sp) (Bool (not b)) store,cds)
          where v@ ~(Bool b) = store M.! sp

interpret EQU  (St (pc,mp,sp,ep,np,store,cds)) =
                St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (v1 == v2)) store,cds)
          where v1 = store M.! (sp-1) 
                v2 = store M.! sp

interpret i@GEQ  st@(St (pc,mp,sp,ep,np,store,cds)) =
      if not (int v1) || not (int v2) 
         then E.throw (NonArithmeticValue i st)
         else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (a1 >= a2)) store,cds)
         where v1@ ~(Int a1) = store M.! (sp-1) 
               v2@ ~(Int a2) = store M.! sp

interpret i@LEQ  st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (int v1) || not (int v2) 
          then E.throw (NonArithmeticValue i st)
          else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (a1 <= a2)) store,cds)
          where v1@ ~(Int a1) = store M.! (sp-1) 
                v2@ ~(Int a2) = store M.! sp

interpret i@LES  st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (int v1) || not (int v2) 
          then E.throw (NonArithmeticValue i st)
          else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (a1 < a2)) store,cds)
          where v1@ ~(Int a1) = store M.! (sp-1) 
                v2@ ~(Int a2) = store M.! sp

interpret i@GRT  st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (int v1) || not (int v2) 
          then E.throw (NonArithmeticValue i st)
          else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (a1 > a2)) store,cds)
          where v1@ ~(Int a1) = store M.! (sp-1) 
                v2@ ~(Int a2) = store M.! sp

interpret NEQ  (St (pc,mp,sp,ep,np,store,cds)) =
                St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Bool (v1 /= v2)) store,cds)
          where v1 = store M.! (sp-1) 
                v2 = store M.! sp

-- Jumps

interpret i@(UJP q) st@(St (pc,mp,sp,ep,np,store,cds)) =
       if q < 0|| q >= cds 
          then E.throw (LabelOutOfRange i st)
          else St (q,mp,sp,ep,np,store,cds)

interpret i@(FJP q) st@(St (pc,mp,sp,ep,np,store,cds)) =
       if q < 0|| q >= cds 
          then E.throw (LabelOutOfRange i st)
          else if not (bool v)
               then E.throw (NonBooleanValue i st)
               else St (if b then pc else q,mp,sp-1,ep,np,store,cds)
          where v@ ~(Bool b) = store M.! sp

interpret i@(IXJ q) st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (int v)
          then E.throw (NonArithmeticValue i st)
          else if a + q < 0|| a + q >= cds 
               then E.throw (LabelOutOfRange i st)
               else St (a + q,mp,sp-1,ep,np,store,cds)
          where v@ ~(Int a) = store M.! sp

interpret i@(CUP p q) st@(St (pc,mp,sp,ep,np,store,cds)) =
       if q < 0|| q >= cds 
          then E.throw (LabelOutOfRange i st)
          else St (q,mp',sp,ep,np,M.insert (mp'+4) (Int pc) store,cds)
          where mp' = sp - (p + 4)


-- Access to arrays and to the heap
       

interpret i@(IXA q)  st@(St (pc,mp,sp,ep,np,store,cds)) =
    if not (int v1) || not (int v2)
       then E.throw (NonArithmeticValue i st)
       else St (pc,mp,sp-1,ep,np,M.insert (sp-1) (Int accum') store,cds)
       where v1@ ~(Int index) = store M.! sp
             v2@ ~(Int accum) = store M.! (sp-1)
             accum'           = accum + index * q

interpret i@(INC q)  st@(St (pc,mp,sp,ep,np,store,cds)) =
    if not (int v)
       then E.throw (NonArithmeticValue i st)
       else St (pc,mp,sp,ep,np,M.insert sp (Int (a+q)) store,cds)
       where v@ ~(Int a) = store M.! sp

interpret i@(DEC q)  st@(St (pc,mp,sp,ep,np,store,cds)) =
    if not (int v)
       then E.throw (NonArithmeticValue i st)
       else St (pc,mp,sp,ep,np,M.insert sp (Int (a-q)) store,cds)
       where v@ ~(Int a) = store M.! sp

interpret i@(CHK p q)  st@(St (_,_,sp,_,_,store,_)) =
    if not (int v)
       then E.throw (NonArithmeticValue i st)
       else if a < p || a > q
            then E.throw (IndexOutOfRange i st)
            else st
       where v@ ~(Int a) = store M.! sp

interpret DPL (St (pc,mp,sp,ep,np,store,cds)) =
               St (pc,mp,sp+1,ep,np,M.insert (sp+1) (store M.! sp) store,cds)

interpret i@(LDD q)  st@(St (pc,mp,sp,ep,np,store,cds)) =
    if not (int addr)
       then E.throw (NonArithmeticValue i st)
       else St (pc,mp,sp+1,ep,np,M.insert (sp+1) (store M.! (a+q)) store,cds)
       where addr@ ~(Int a) = store M.! (sp-2)

interpret SLI (St (pc,mp,sp,ep,np,store,cds)) =
               St (pc,mp,sp-1,ep,np,M.insert (sp-1) (store M.! sp) store,cds)
       
interpret i@NEW  st@(St (pc,mp,sp,ep,np,store,cds)) =
    if not (int v1) || not (int v2)
       then E.throw (AddressNotInt i st)
       else if addr > sp && addr < np
            then E.throw (AdressOutOfRange i st) 
            else if np - size <= ep
                 then E.throw (StackHeapCollision i st)
                 else St (pc,mp,sp-2,ep,np-size,store',cds)
       where v1@ ~(Int size) = store M.! sp
             v2@ ~(Int addr) = store M.! (sp-1)
             store'          = M.insert addr (Int (np-size)) store


-- Procedures, functions, and main program 


interpret i@(MOVS q)  st@(St (pc,mp,sp,ep,np,store,cds)) =
   if not (int addr)
      then E.throw (NonArithmeticValue i st)
      else St (pc,mp,sp+q-1,ep,np,store',cds)
   where addr@ ~(Int a) = store M.! sp
         store'         = foldr (\i sto -> M.insert (sp+i) (sto M.! (a+i)) sto) 
                                store [0..q-1]

interpret i@(MOVD q)  st@(St (pc,mp,sp,ep,np,store,cds)) =
   if not (int desc) || not (int addr) || not (int size) || not (int inc) 
      then E.throw (NonArithmeticValue i st)
      else St (pc,mp,sp+n,ep,np,store',cds)
   where desc@ ~(Int d) = store M.! (mp + q)
         addr@ ~(Int a) = store M.! d
         size@ ~(Int n) = store M.! (d + 1)
         inc @ ~(Int m) = store M.! (d + 2)
         store'         = foldr (\i sto -> M.insert (sp+i) (sto M.! (a+m+i)) sto) 
                                store [0..n-1]

interpret i@(MST p) st@(St (pc,mp,sp,ep,np,store,cds)) =
          if addr > sp || addr >= stSize
             then E.throw (AdressOutOfRange i st) 
             else St (pc,mp,sp+5,ep,np,store3,cds) 
          where addr   = base i st p mp
                store1 = M.insert (sp+2) (Int addr) store
                store2 = M.insert (sp+3) (Int mp)   store1
                store3 = M.insert (sp+4) (Int ep)   store2

interpret (SSP p) (St (pc,mp,sp    ,ep,np,store,cds)) =
                   St (pc,mp,mp+p-1,ep,np,store,cds)

interpret i@(SEP p) st@(St (pc,mp,sp,ep,np,store,cds)) =
       if sp + p >= np
          then E.throw (StackHeapCollision i st)
          else St (pc,mp,sp,sp + p,np,store,cds)
                  
interpret i@RETF  st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (int v1) || not (int v2) || not (int v3)
       then E.throw (AddressNotInt i st)
       else if ep' >= np 
               then E.throw (StackHeapCollision i st)
               else St (pc',mp',mp,ep',np,store,cds)
       where v1@ ~(Int ep') = store M.! (mp + 3)
             v2@ ~(Int pc') = store M.! (mp + 4)          
             v3@ ~(Int mp') = store M.! (mp + 2)          

interpret i@RETP  st@(St (pc,mp,sp,ep,np,store,cds)) =
       if not (int v1) || not (int v2) || not (int v3)
       then E.throw (AddressNotInt i st)
       else if ep' >= np 
               then E.throw (StackHeapCollision i st)
               else St (pc',mp',mp-1,ep',np,store,cds)
       where v1@ ~(Int ep') = store M.! (mp + 3)
             v2@ ~(Int pc') = store M.! (mp + 4)          
             v3@ ~(Int mp') = store M.! (mp + 2)          












