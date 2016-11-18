package com.rion.imagereader;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Criado por rogerio.junior em 18/11/2016.
 */

@Qualifier @Retention(RUNTIME)
public @interface ForApplication {
}