package com.javax0.fluflu.processor;

import javax.lang.model.element.ExecutableElement;

/**
 * A simple data class that holds a method and a target state. The method
 * represents a transition from one state to the target state. Although
 * generally a transition goes form one state to another there is no reason to
 * store the source state (from which the method transits to the target state),
 * because the transitions are stored in a map that has the source transitions
 * as keys and the values are lists of the edges.
 * 
 * @author Peter Verhas
 * 
 */
class TransitionEdge {

  ExecutableElement method;
  String            targetState;
  String            name;
  boolean           end;

  TransitionEdge(ExecutableElement method, String targetState, String name, boolean end) {
    this.method = method;
    this.targetState = targetState;
    this.name = name;
    this.end = end;
  }
}
