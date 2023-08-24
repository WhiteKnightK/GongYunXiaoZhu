import java.text.SimpleDateFormat
import java.util.*

class TimeStampConverter {
    companion object {
        fun convertTimeStampToDateString(timeStamp: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String {
            try {
                val date = Date(timeStamp) // Remove the multiplication by 1000
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                return sdf.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        fun getCurrentTimeStamp(): Long {
            return System.currentTimeMillis() // Remove the division by 1000
        }
    }
}



//fun main() {
//    val timeStamp = 1689042014000 // Replace this with your desired timestamp
//    val formattedDate = TimeStampConverter.convertTimeStampToDateString(timeStamp)
//    println("Formatted Date: $formattedDate")
//}
