package org.gracilianomp.arithmetic;

 public interface MathValueInteger extends MathValue {

     @Override
     default boolean isDecimal() {
         return false;
     }

     @Override
     default boolean isInteger() {
         return true ;
     }

 }
