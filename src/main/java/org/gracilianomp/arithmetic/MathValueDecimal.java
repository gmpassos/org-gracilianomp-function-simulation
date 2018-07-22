package org.gracilianomp.arithmetic;

 public interface MathValueDecimal extends MathValue {

     @Override
     default boolean isDecimal() {
         return true;
     }

     @Override
     default boolean isInteger() {
         return false ;
     }

 }
