package poc.microprofile.annotation

/**
 * Put this annotation on the data model which will be used for deserialization
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DeserializableModel