package com.carplots.common.groovy.aop

import sun.org.mozilla.classfile.SuperBlock;

class AbstractAopObject extends GroovyObjectSupport
	implements GroovyInterceptable {

		def after = [:]
		def afterAcceptor = null		
		
		def before = [:]		
		def beforeAcceptor = null		
		
		def around = [:]		
		def aroundAcceptor = null
		
		public Object invokeMethod(String name, Object args) {
			
			def result = null
			def callDetails = [methodName:name, args:args, called:this ]							
			
			Closure beforeAcceptorAction = (beforeAcceptor == null)?
				before[name] : beforeAcceptor(callDetails)
			
			if (beforeAcceptorAction != null) {
				beforeAcceptorAction(callDetails)
			}
									
			Closure aroundAcceptorAction = (aroundAcceptor == null)?
				around[name] : aroundAcceptor(callDetails)							
			if (aroundAcceptorAction != null) {
				def proceed = {
					super.invokeMethod(name, args)
				}
				result = aroundAcceptorAction[name](callDetails, proceed)
			}
			else {
				result = super.invokeMethod(name, args)
			}
			
			Closure afterAcceptorAction = (afterAcceptor == null)?
				after[name] : afterAcceptor(callDetails)				
			if (afterAcceptorAction != null)
				afterAcceptorAction[name](callDetails)
			
			return result
		}		
}
