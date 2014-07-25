package com.sun.jmx.snmp.IPAcl;
import java.io.*;
class ParserTokenManager implements ParserConstants {
  private final int jjStopStringLiteralDfa_0(  int pos,  long active0){
switch (pos) {
case 0:
      if ((active0 & 0x8000L) != 0L)       return 0;
    if ((active0 & 0xfe5000L) != 0L) {
      jjmatchedKind=31;
      return 47;
    }
  if ((active0 & 0xd80L) != 0L) {
    jjmatchedKind=31;
    return 48;
  }
return -1;
case 1:
if ((active0 & 0xfe5c00L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=1;
return 49;
}
if ((active0 & 0x180L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=1;
return 50;
}
return -1;
case 2:
if ((active0 & 0xfe5c00L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=2;
return 49;
}
if ((active0 & 0x100L) != 0L) return 49;
if ((active0 & 0x80L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=2;
return 50;
}
return -1;
case 3:
if ((active0 & 0x565c00L) != 0L) {
if (jjmatchedPos != 3) {
jjmatchedKind=31;
jjmatchedPos=3;
}
return 49;
}
if ((active0 & 0xa80000L) != 0L) return 49;
if ((active0 & 0x80L) != 0L) {
if (jjmatchedPos != 3) {
jjmatchedKind=31;
jjmatchedPos=3;
}
return 50;
}
return -1;
case 4:
if ((active0 & 0xa00000L) != 0L) return 51;
if ((active0 & 0x60000L) != 0L) {
if (jjmatchedPos < 3) {
jjmatchedKind=31;
jjmatchedPos=3;
}
return 51;
}
if ((active0 & 0x1000L) != 0L) return 49;
if ((active0 & 0x504c80L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=4;
return 49;
}
return -1;
case 5:
if ((active0 & 0x500080L) != 0L) return 49;
if ((active0 & 0x4c00L) != 0L) {
if (jjmatchedPos != 5) {
jjmatchedKind=31;
jjmatchedPos=5;
}
return 49;
}
if ((active0 & 0xa60000L) != 0L) {
if (jjmatchedPos != 5) {
jjmatchedKind=31;
jjmatchedPos=5;
}
return 51;
}
return -1;
case 6:
if ((active0 & 0x400000L) != 0L) return 51;
if ((active0 & 0x4c00L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=6;
return 49;
}
if ((active0 & 0xa60000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=6;
return 51;
}
return -1;
case 7:
if ((active0 & 0x660000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=7;
return 51;
}
if ((active0 & 0x800000L) != 0L) return 51;
if ((active0 & 0x4000L) != 0L) return 49;
if ((active0 & 0xc00L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=7;
return 49;
}
return -1;
case 8:
if ((active0 & 0x20000L) != 0L) return 51;
if ((active0 & 0xc00L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=8;
return 49;
}
if ((active0 & 0x640000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=8;
return 51;
}
return -1;
case 9:
if ((active0 & 0x40000L) != 0L) return 51;
if ((active0 & 0x800L) != 0L) return 49;
if ((active0 & 0x600000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=9;
return 51;
}
if ((active0 & 0x400L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=9;
return 49;
}
return -1;
case 10:
if ((active0 & 0x600000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=10;
return 51;
}
if ((active0 & 0x400L) != 0L) return 49;
return -1;
case 11:
if ((active0 & 0x600000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=11;
return 51;
}
return -1;
case 12:
if ((active0 & 0x600000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=12;
return 51;
}
return -1;
case 13:
if ((active0 & 0x400000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=13;
return 51;
}
if ((active0 & 0x200000L) != 0L) return 51;
return -1;
case 14:
if ((active0 & 0x400000L) != 0L) {
jjmatchedKind=31;
jjmatchedPos=14;
return 51;
}
return -1;
default :
return -1;
}
}
private final int jjStartNfa_0(int pos,long active0){
return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos,active0),pos + 1);
}
private final int jjStopAtPos(int pos,int kind){
jjmatchedKind=kind;
jjmatchedPos=pos;
return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos,int kind,int state){
jjmatchedKind=kind;
jjmatchedPos=pos;
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
return pos + 1;
}
return jjMoveNfa_0(state,pos + 1);
}
private final int jjMoveStringLiteralDfa0_0(){
switch (curChar) {
case 33:
return jjStopAtPos(0,38);
case 44:
return jjStopAtPos(0,36);
case 45:
return jjStartNfaWithStates_0(0,15,0);
case 46:
return jjStopAtPos(0,37);
case 47:
return jjStopAtPos(0,39);
case 61:
return jjStopAtPos(0,9);
case 97:
return jjMoveStringLiteralDfa1_0(0x180L);
case 99:
return jjMoveStringLiteralDfa1_0(0x400L);
case 101:
return jjMoveStringLiteralDfa1_0(0x800L);
case 104:
return jjMoveStringLiteralDfa1_0(0x1000L);
case 105:
return jjMoveStringLiteralDfa1_0(0x500000L);
case 109:
return jjMoveStringLiteralDfa1_0(0x4000L);
case 114:
return jjMoveStringLiteralDfa1_0(0x60000L);
case 116:
return jjMoveStringLiteralDfa1_0(0xa80000L);
case 123:
return jjStopAtPos(0,13);
case 125:
return jjStopAtPos(0,16);
default :
return jjMoveNfa_0(5,0);
}
}
private final int jjMoveStringLiteralDfa1_0(long active0){
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(0,active0);
return 1;
}
switch (curChar) {
case 97:
return jjMoveStringLiteralDfa2_0(active0,0x4000L);
case 99:
return jjMoveStringLiteralDfa2_0(active0,0x180L);
case 101:
return jjMoveStringLiteralDfa2_0(active0,0x60000L);
case 110:
return jjMoveStringLiteralDfa2_0(active0,0x500800L);
case 111:
return jjMoveStringLiteralDfa2_0(active0,0x1400L);
case 114:
return jjMoveStringLiteralDfa2_0(active0,0xa80000L);
default :
break;
}
return jjStartNfa_0(0,active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(0,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(1,active0);
return 2;
}
switch (curChar) {
case 97:
return jjMoveStringLiteralDfa3_0(active0,0xae0000L);
case 99:
return jjMoveStringLiteralDfa3_0(active0,0x80L);
case 102:
return jjMoveStringLiteralDfa3_0(active0,0x500000L);
case 108:
if ((active0 & 0x100L) != 0L) return jjStartNfaWithStates_0(2,8,49);
break;
case 109:
return jjMoveStringLiteralDfa3_0(active0,0x400L);
case 110:
return jjMoveStringLiteralDfa3_0(active0,0x4000L);
case 115:
return jjMoveStringLiteralDfa3_0(active0,0x1000L);
case 116:
return jjMoveStringLiteralDfa3_0(active0,0x800L);
default :
break;
}
return jjStartNfa_0(1,active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(1,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(2,active0);
return 3;
}
switch (curChar) {
case 97:
return jjMoveStringLiteralDfa4_0(active0,0x4000L);
case 100:
return jjMoveStringLiteralDfa4_0(active0,0x60000L);
case 101:
return jjMoveStringLiteralDfa4_0(active0,0x880L);
case 109:
return jjMoveStringLiteralDfa4_0(active0,0x400L);
case 111:
return jjMoveStringLiteralDfa4_0(active0,0x500000L);
case 112:
if ((active0 & 0x80000L) != 0L) {
jjmatchedKind=19;
jjmatchedPos=3;
}
return jjMoveStringLiteralDfa4_0(active0,0xa00000L);
case 116:
return jjMoveStringLiteralDfa4_0(active0,0x1000L);
default :
break;
}
return jjStartNfa_0(2,active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(2,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(3,active0);
return 4;
}
switch (curChar) {
case 45:
return jjMoveStringLiteralDfa5_0(active0,0xa60000L);
case 103:
return jjMoveStringLiteralDfa5_0(active0,0x4000L);
case 114:
return jjMoveStringLiteralDfa5_0(active0,0x500800L);
case 115:
if ((active0 & 0x1000L) != 0L) return jjStartNfaWithStates_0(4,12,49);
return jjMoveStringLiteralDfa5_0(active0,0x80L);
case 117:
return jjMoveStringLiteralDfa5_0(active0,0x400L);
default :
break;
}
return jjStartNfa_0(3,active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(3,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(4,active0);
return 5;
}
switch (curChar) {
case 99:
return jjMoveStringLiteralDfa6_0(active0,0x200000L);
case 101:
return jjMoveStringLiteralDfa6_0(active0,0x4000L);
case 109:
if ((active0 & 0x100000L) != 0L) {
jjmatchedKind=20;
jjmatchedPos=5;
}
return jjMoveStringLiteralDfa6_0(active0,0x400000L);
case 110:
return jjMoveStringLiteralDfa6_0(active0,0x800400L);
case 111:
return jjMoveStringLiteralDfa6_0(active0,0x20000L);
case 112:
return jjMoveStringLiteralDfa6_0(active0,0x800L);
case 115:
if ((active0 & 0x80L) != 0L) return jjStartNfaWithStates_0(5,7,49);
break;
case 119:
return jjMoveStringLiteralDfa6_0(active0,0x40000L);
default :
break;
}
return jjStartNfa_0(4,active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(4,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(5,active0);
return 6;
}
switch (curChar) {
case 45:
return jjMoveStringLiteralDfa7_0(active0,0x400000L);
case 105:
return jjMoveStringLiteralDfa7_0(active0,0x400L);
case 110:
return jjMoveStringLiteralDfa7_0(active0,0x20000L);
case 111:
return jjMoveStringLiteralDfa7_0(active0,0x200000L);
case 114:
return jjMoveStringLiteralDfa7_0(active0,0x44800L);
case 117:
return jjMoveStringLiteralDfa7_0(active0,0x800000L);
default :
break;
}
return jjStartNfa_0(5,active0);
}
private final int jjMoveStringLiteralDfa7_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(5,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(6,active0);
return 7;
}
switch (curChar) {
case 99:
return jjMoveStringLiteralDfa8_0(active0,0x400000L);
case 105:
return jjMoveStringLiteralDfa8_0(active0,0x40800L);
case 108:
return jjMoveStringLiteralDfa8_0(active0,0x20000L);
case 109:
if ((active0 & 0x800000L) != 0L) return jjStartNfaWithStates_0(7,23,51);
return jjMoveStringLiteralDfa8_0(active0,0x200000L);
case 115:
if ((active0 & 0x4000L) != 0L) return jjStartNfaWithStates_0(7,14,49);
break;
case 116:
return jjMoveStringLiteralDfa8_0(active0,0x400L);
default :
break;
}
return jjStartNfa_0(6,active0);
}
private final int jjMoveStringLiteralDfa8_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(6,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(7,active0);
return 8;
}
switch (curChar) {
case 105:
return jjMoveStringLiteralDfa9_0(active0,0x400L);
case 109:
return jjMoveStringLiteralDfa9_0(active0,0x200000L);
case 111:
return jjMoveStringLiteralDfa9_0(active0,0x400000L);
case 115:
return jjMoveStringLiteralDfa9_0(active0,0x800L);
case 116:
return jjMoveStringLiteralDfa9_0(active0,0x40000L);
case 121:
if ((active0 & 0x20000L) != 0L) return jjStartNfaWithStates_0(8,17,51);
break;
default :
break;
}
return jjStartNfa_0(7,active0);
}
private final int jjMoveStringLiteralDfa9_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(7,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(8,active0);
return 9;
}
switch (curChar) {
case 101:
if ((active0 & 0x800L) != 0L) return jjStartNfaWithStates_0(9,11,49);
 else if ((active0 & 0x40000L) != 0L) return jjStartNfaWithStates_0(9,18,51);
return jjMoveStringLiteralDfa10_0(active0,0x400L);
case 109:
return jjMoveStringLiteralDfa10_0(active0,0x400000L);
case 117:
return jjMoveStringLiteralDfa10_0(active0,0x200000L);
default :
break;
}
return jjStartNfa_0(8,active0);
}
private final int jjMoveStringLiteralDfa10_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(8,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(9,active0);
return 10;
}
switch (curChar) {
case 109:
return jjMoveStringLiteralDfa11_0(active0,0x400000L);
case 110:
return jjMoveStringLiteralDfa11_0(active0,0x200000L);
case 115:
if ((active0 & 0x400L) != 0L) return jjStartNfaWithStates_0(10,10,49);
break;
default :
break;
}
return jjStartNfa_0(9,active0);
}
private final int jjMoveStringLiteralDfa11_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(9,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(10,active0);
return 11;
}
switch (curChar) {
case 105:
return jjMoveStringLiteralDfa12_0(active0,0x200000L);
case 117:
return jjMoveStringLiteralDfa12_0(active0,0x400000L);
default :
break;
}
return jjStartNfa_0(10,active0);
}
private final int jjMoveStringLiteralDfa12_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(10,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(11,active0);
return 12;
}
switch (curChar) {
case 110:
return jjMoveStringLiteralDfa13_0(active0,0x400000L);
case 116:
return jjMoveStringLiteralDfa13_0(active0,0x200000L);
default :
break;
}
return jjStartNfa_0(11,active0);
}
private final int jjMoveStringLiteralDfa13_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(11,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(12,active0);
return 13;
}
switch (curChar) {
case 105:
return jjMoveStringLiteralDfa14_0(active0,0x400000L);
case 121:
if ((active0 & 0x200000L) != 0L) return jjStartNfaWithStates_0(13,21,51);
break;
default :
break;
}
return jjStartNfa_0(12,active0);
}
private final int jjMoveStringLiteralDfa14_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(12,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(13,active0);
return 14;
}
switch (curChar) {
case 116:
return jjMoveStringLiteralDfa15_0(active0,0x400000L);
default :
break;
}
return jjStartNfa_0(13,active0);
}
private final int jjMoveStringLiteralDfa15_0(long old0,long active0){
if (((active0&=old0)) == 0L) return jjStartNfa_0(13,old0);
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
jjStopStringLiteralDfa_0(14,active0);
return 15;
}
switch (curChar) {
case 121:
if ((active0 & 0x400000L) != 0L) return jjStartNfaWithStates_0(15,22,51);
break;
default :
break;
}
return jjStartNfa_0(14,active0);
}
private final void jjCheckNAdd(int state){
if (jjrounds[state] != jjround) {
jjstateSet[jjnewStateCnt++]=state;
jjrounds[state]=jjround;
}
}
private final void jjAddStates(int start,int end){
do {
jjstateSet[jjnewStateCnt++]=jjnextStates[start];
}
 while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1,int state2){
jjCheckNAdd(state1);
jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start,int end){
do {
jjCheckNAdd(jjnextStates[start]);
}
 while (start++ != end);
}
private final void jjCheckNAddStates(int start){
jjCheckNAdd(jjnextStates[start]);
jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0={0x0L,0x0L,0xffffffffffffffffL,0xffffffffffffffffL};
private final int jjMoveNfa_0(int startState,int curPos){
int[] nextStates;
int startsAt=0;
jjnewStateCnt=47;
int i=1;
jjstateSet[0]=startState;
int j, kind=0x7fffffff;
for (; ; ) {
if (++jjround == 0x7fffffff) ReInitRounds();
if (curChar < 64) {
long l=1L << curChar;
MatchLoop: do {
switch (jjstateSet[--i]) {
case 49:
if ((0x3ff200000000000L & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(19);
}
break;
case 48:
if ((0x3ff200000000000L & l) != 0L) jjCheckNAddTwoStates(18,19);
 else if (curChar == 58) jjCheckNAddStates(3,5);
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
 else if (curChar == 58) jjCheckNAddTwoStates(23,25);
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(26,27);
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(23,24);
break;
case 47:
if ((0x3ff200000000000L & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
break;
case 50:
if ((0x3ff200000000000L & l) != 0L) jjCheckNAddTwoStates(18,19);
 else if (curChar == 58) jjCheckNAddStates(3,5);
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
 else if (curChar == 58) jjCheckNAddTwoStates(23,25);
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(19);
}
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(26,27);
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(23,24);
break;
case 5:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddStates(6,9);
 else if (curChar == 58) jjAddStates(10,11);
 else if (curChar == 34) jjCheckNAddTwoStates(15,16);
 else if (curChar == 35) jjCheckNAddStates(12,14);
 else if (curChar == 45) jjstateSet[jjnewStateCnt++]=0;
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(15,17);
}
if ((0x3fe000000000000L & l) != 0L) {
if (kind > 24) kind=24;
jjCheckNAddTwoStates(12,13);
}
 else if (curChar == 48) {
if (kind > 24) kind=24;
jjCheckNAddStates(18,20);
}
break;
case 51:
if ((0x3ff200000000000L & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x3ff000000000000L & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(19);
}
break;
case 0:
if (curChar == 45) jjCheckNAddStates(21,23);
break;
case 1:
if ((0xffffffffffffdbffL & l) != 0L) jjCheckNAddStates(21,23);
break;
case 2:
if ((0x2400L & l) != 0L && kind > 5) kind=5;
break;
case 3:
if (curChar == 10 && kind > 5) kind=5;
break;
case 4:
if (curChar == 13) jjstateSet[jjnewStateCnt++]=3;
break;
case 6:
if (curChar == 35) jjCheckNAddStates(12,14);
break;
case 7:
if ((0xffffffffffffdbffL & l) != 0L) jjCheckNAddStates(12,14);
break;
case 8:
if ((0x2400L & l) != 0L && kind > 6) kind=6;
break;
case 9:
if (curChar == 10 && kind > 6) kind=6;
break;
case 10:
if (curChar == 13) jjstateSet[jjnewStateCnt++]=9;
break;
case 11:
if ((0x3fe000000000000L & l) == 0L) break;
if (kind > 24) kind=24;
jjCheckNAddTwoStates(12,13);
break;
case 12:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 24) kind=24;
jjCheckNAddTwoStates(12,13);
break;
case 14:
if (curChar == 34) jjCheckNAddTwoStates(15,16);
break;
case 15:
if ((0xfffffffbffffffffL & l) != 0L) jjCheckNAddTwoStates(15,16);
break;
case 16:
if (curChar == 34 && kind > 35) kind=35;
break;
case 17:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAddStates(15,17);
break;
case 18:
if ((0x3ff200000000000L & l) != 0L) jjCheckNAddTwoStates(18,19);
break;
case 19:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAdd(19);
break;
case 20:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAdd(20);
break;
case 21:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
break;
case 22:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddStates(6,9);
break;
case 23:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(23,24);
break;
case 24:
if (curChar == 58) jjCheckNAddTwoStates(23,25);
break;
case 25:
case 41:
if (curChar == 58 && kind > 28) kind=28;
break;
case 26:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(26,27);
break;
case 27:
if (curChar == 58) jjCheckNAddStates(3,5);
break;
case 28:
case 42:
if (curChar == 58) jjCheckNAddTwoStates(29,36);
break;
case 29:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(29,30);
break;
case 30:
if (curChar == 46) jjCheckNAdd(31);
break;
case 31:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(31,32);
break;
case 32:
if (curChar == 46) jjCheckNAdd(33);
break;
case 33:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(33,34);
break;
case 34:
if (curChar == 46) jjCheckNAdd(35);
break;
case 35:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 28) kind=28;
jjCheckNAdd(35);
break;
case 36:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 28) kind=28;
jjCheckNAddStates(24,26);
break;
case 37:
if ((0x3ff000000000000L & l) != 0L) jjCheckNAddTwoStates(37,28);
break;
case 38:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 28) kind=28;
jjCheckNAdd(38);
break;
case 39:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 28) kind=28;
jjCheckNAddStates(27,31);
break;
case 40:
if (curChar == 58) jjAddStates(10,11);
break;
case 43:
if (curChar != 48) break;
if (kind > 24) kind=24;
jjCheckNAddStates(18,20);
break;
case 45:
if ((0x3ff000000000000L & l) == 0L) break;
if (kind > 24) kind=24;
jjCheckNAddTwoStates(45,13);
break;
case 46:
if ((0xff000000000000L & l) == 0L) break;
if (kind > 24) kind=24;
jjCheckNAddTwoStates(46,13);
break;
default :
break;
}
}
 while (i != startsAt);
}
 else if (curChar < 128) {
long l=1L << (curChar & 077);
MatchLoop: do {
switch (jjstateSet[--i]) {
case 49:
if ((0x7fffffe87fffffeL & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(19);
}
break;
case 48:
if ((0x7fffffe87fffffeL & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
if ((0x7e0000007eL & l) != 0L) jjCheckNAddTwoStates(26,27);
if ((0x7e0000007eL & l) != 0L) jjCheckNAddTwoStates(23,24);
break;
case 47:
if ((0x7fffffe87fffffeL & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
break;
case 50:
if ((0x7fffffe87fffffeL & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
}
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(20);
}
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(19);
}
if ((0x7e0000007eL & l) != 0L) jjCheckNAddTwoStates(26,27);
if ((0x7e0000007eL & l) != 0L) jjCheckNAddTwoStates(23,24);
break;
case 5:
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAddStates(15,17);
}
if ((0x7e0000007eL & l) != 0L) jjCheckNAddStates(6,9);
break;
case 51:
if ((0x7fffffe87fffffeL & l) != 0L) jjCheckNAddTwoStates(18,19);
if ((0x7fffffe07fffffeL & l) != 0L) {
if (kind > 31) kind=31;
jjCheckNAdd(19);
}
break;
case 1:
jjAddStates(21,23);
break;
case 7:
jjAddStates(12,14);
break;
case 13:
if ((0x100000001000L & l) != 0L && kind > 24) kind=24;
break;
case 15:
jjAddStates(32,33);
break;
case 17:
if ((0x7fffffe07fffffeL & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAddStates(15,17);
break;
case 18:
if ((0x7fffffe87fffffeL & l) != 0L) jjCheckNAddTwoStates(18,19);
break;
case 19:
if ((0x7fffffe07fffffeL & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAdd(19);
break;
case 20:
if ((0x7fffffe07fffffeL & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAdd(20);
break;
case 21:
if ((0x7fffffe07fffffeL & l) == 0L) break;
if (kind > 31) kind=31;
jjCheckNAddStates(0,2);
break;
case 22:
if ((0x7e0000007eL & l) != 0L) jjCheckNAddStates(6,9);
break;
case 23:
if ((0x7e0000007eL & l) != 0L) jjCheckNAddTwoStates(23,24);
break;
case 26:
if ((0x7e0000007eL & l) != 0L) jjCheckNAddTwoStates(26,27);
break;
case 36:
if ((0x7e0000007eL & l) == 0L) break;
if (kind > 28) kind=28;
jjCheckNAddStates(24,26);
break;
case 37:
if ((0x7e0000007eL & l) != 0L) jjCheckNAddTwoStates(37,28);
break;
case 38:
if ((0x7e0000007eL & l) == 0L) break;
if (kind > 28) kind=28;
jjCheckNAdd(38);
break;
case 39:
if ((0x7e0000007eL & l) == 0L) break;
if (kind > 28) kind=28;
jjCheckNAddStates(27,31);
break;
case 44:
if ((0x100000001000000L & l) != 0L) jjCheckNAdd(45);
break;
case 45:
if ((0x7e0000007eL & l) == 0L) break;
if (kind > 24) kind=24;
jjCheckNAddTwoStates(45,13);
break;
default :
break;
}
}
 while (i != startsAt);
}
 else {
int i2=(curChar & 0xff) >> 6;
long l2=1L << (curChar & 077);
MatchLoop: do {
switch (jjstateSet[--i]) {
case 1:
if ((jjbitVec0[i2] & l2) != 0L) jjAddStates(21,23);
break;
case 7:
if ((jjbitVec0[i2] & l2) != 0L) jjAddStates(12,14);
break;
case 15:
if ((jjbitVec0[i2] & l2) != 0L) jjAddStates(32,33);
break;
default :
break;
}
}
 while (i != startsAt);
}
if (kind != 0x7fffffff) {
jjmatchedKind=kind;
jjmatchedPos=curPos;
kind=0x7fffffff;
}
++curPos;
if ((i=jjnewStateCnt) == (startsAt=47 - (jjnewStateCnt=startsAt))) return curPos;
try {
curChar=input_stream.readChar();
}
 catch (java.io.IOException e) {
return curPos;
}
}
}
static final int[] jjnextStates={18,19,21,28,29,39,23,24,26,27,41,42,7,8,10,18,20,21,44,46,13,1,2,4,37,28,38,26,27,37,28,38,15,16};
public static final String[] jjstrLiteralImages={"",null,null,null,null,null,null,"\141\143\143\145\163\163","\141\143\154","\75","\143\157\155\155\165\156\151\164\151\145\163","\145\156\164\145\162\160\162\151\163\145","\150\157\163\164\163","\173","\155\141\156\141\147\145\162\163","\55","\175","\162\145\141\144\55\157\156\154\171","\162\145\141\144\55\167\162\151\164\145","\164\162\141\160","\151\156\146\157\162\155","\164\162\141\160\55\143\157\155\155\165\156\151\164\171","\151\156\146\157\162\155\55\143\157\155\155\165\156\151\164\171","\164\162\141\160\55\156\165\155",null,null,null,null,null,null,null,null,null,null,null,null,"\54","\56","\41","\57"};
public static final String[] lexStateNames={"DEFAULT"};
static final long[] jjtoToken={0xf891ffff81L};
static final long[] jjtoSkip={0x7eL};
private ASCII_CharStream input_stream;
private final int[] jjrounds=new int[47];
private final int[] jjstateSet=new int[94];
protected char curChar;
public ParserTokenManager(ASCII_CharStream stream){
if (ASCII_CharStream.staticFlag) throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
input_stream=stream;
}
public ParserTokenManager(ASCII_CharStream stream,int lexState){
this(stream);
SwitchTo(lexState);
}
public void ReInit(ASCII_CharStream stream){
jjmatchedPos=jjnewStateCnt=0;
curLexState=defaultLexState;
input_stream=stream;
ReInitRounds();
}
private final void ReInitRounds(){
int i;
jjround=0x80000001;
for (i=47; i-- > 0; ) jjrounds[i]=0x80000000;
}
public void ReInit(ASCII_CharStream stream,int lexState){
ReInit(stream);
SwitchTo(lexState);
}
public void SwitchTo(int lexState){
if (lexState >= 1 || lexState < 0) throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.",TokenMgrError.INVALID_LEXICAL_STATE);
 else curLexState=lexState;
}
private final Token jjFillToken(){
Token t=Token.newToken(jjmatchedKind);
t.kind=jjmatchedKind;
String im=jjstrLiteralImages[jjmatchedKind];
t.image=(im == null) ? input_stream.GetImage() : im;
t.beginLine=input_stream.getBeginLine();
t.beginColumn=input_stream.getBeginColumn();
t.endLine=input_stream.getEndLine();
t.endColumn=input_stream.getEndColumn();
return t;
}
int curLexState=0;
int defaultLexState=0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;
public final Token getNextToken(){
int kind;
Token specialToken=null;
Token matchedToken;
int curPos=0;
EOFLoop: for (; ; ) {
try {
curChar=input_stream.BeginToken();
}
 catch (java.io.IOException e) {
jjmatchedKind=0;
matchedToken=jjFillToken();
return matchedToken;
}
try {
input_stream.backup(0);
while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L) curChar=input_stream.BeginToken();
}
 catch (java.io.IOException e1) {
continue EOFLoop;
}
jjmatchedKind=0x7fffffff;
jjmatchedPos=0;
curPos=jjMoveStringLiteralDfa0_0();
if (jjmatchedKind != 0x7fffffff) {
if (jjmatchedPos + 1 < curPos) input_stream.backup(curPos - jjmatchedPos - 1);
if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
matchedToken=jjFillToken();
return matchedToken;
}
 else {
continue EOFLoop;
}
}
int error_line=input_stream.getEndLine();
int error_column=input_stream.getEndColumn();
String error_after=null;
boolean EOFSeen=false;
try {
input_stream.readChar();
input_stream.backup(1);
}
 catch (java.io.IOException e1) {
EOFSeen=true;
error_after=curPos <= 1 ? "" : input_stream.GetImage();
if (curChar == '\n' || curChar == '\r') {
error_line++;
error_column=0;
}
 else error_column++;
}
if (!EOFSeen) {
input_stream.backup(1);
error_after=curPos <= 1 ? "" : input_stream.GetImage();
}
throw new TokenMgrError(EOFSeen,curLexState,error_line,error_column,error_after,curChar,TokenMgrError.LEXICAL_ERROR);
}
}
}
