package cangjie.scale.sorting.scale


interface DeviceFilter {
    
    /** 运行 */
    fun allow(device: String): Boolean


}