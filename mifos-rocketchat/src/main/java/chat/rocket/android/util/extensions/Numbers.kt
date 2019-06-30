package chat.rocket.android.util.extensions

import DateTimeHelper
import org.threeten.bp.LocalDateTime

fun Long?.localDateTime(): LocalDateTime? {
    return this?.let {
        DateTimeHelper.getLocalDateTime(it)
    }
}