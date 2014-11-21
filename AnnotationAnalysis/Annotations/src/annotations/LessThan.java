package annotations;

public @interface LessThan {
	int bound() default 0;
}
