package com.cos.logtest.config.aop.warn;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
@Aspect
public class BindingAdvisce {
	
	//sysout이랑 비슷 log에 level이라는게 있다. 
	private static final Logger log = LoggerFactory.getLogger(BindingAdvisce.class);


	@Around("execution(* com.cos.logtest.controller..*Controller.*(..))")
	public Object bindingPrint(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		Object[] args = proceedingJoinPoint.getArgs();
		
		String type = proceedingJoinPoint.getSignature().getDeclaringTypeName()+" : "; // 어느 컨트롤러에서
		String methodName = proceedingJoinPoint.getSignature().getName()+"() "; //어즈 메서드에서 오류났나
		
		for(Object arg : args) {
			if(arg instanceof BindingResult) {
				BindingResult bindingResult = (BindingResult)arg;
				if(bindingResult.hasErrors()) {
					Map<String, String> errorMap = new HashMap<>();
					for(FieldError error : bindingResult.getFieldErrors()) {
						errorMap.put(error.getField(), error.getDefaultMessage());
						log.debug("debug찍혀?");
						log.info("info 찍혀?");
						log.warn(type+methodName+error.getDefaultMessage()); //  공통관심사 -> aop
						log.error("error 찍혀?");
						//여기서 센트리 던지든 하면 된다.
					}
					System.out.println("뭔가 잘못들어왔어요");
					return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
				}
			}
		}
		
		return proceedingJoinPoint.proceed();
	}
}
