package com.conas.novel

object ResourceUtils {

    /**
     * Get the resource file as a byte array
     *
     * @param fileName name of the file
     * @param clazz class to use the classloader from
     * @return byte array of the file
     */
    @JvmStatic
    fun getResource(fileName: String, clazz: Class<*> = this::class.java): ByteArray {
        return clazz.classLoader.getResource(fileName)?.readBytes()
            ?: throw IllegalArgumentException("File $fileName not found")
    }

    /**
     * Get the resource file as a string
     *
     * @param fileName name of the file
     * @return string of the file
     */
    @JvmStatic
    fun getResourceAsString(fileName: String): String {
        return getResource(fileName).toString(Charsets.UTF_8)
    }
}