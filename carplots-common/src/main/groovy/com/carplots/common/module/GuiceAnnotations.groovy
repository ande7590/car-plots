package com.carplots.common.module

import com.google.inject.BindingAnnotation;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target([FIELD, METHOD, PARAMETER])
@BindingAnnotation
	@interface Scraper {
}
	
@Retention(RetentionPolicy.RUNTIME)
@Target([FIELD, METHOD, PARAMETER])
@BindingAnnotation
	@interface CarMeta {
}
