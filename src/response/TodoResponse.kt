package kim.wonjun.response

import kim.wonjun.Todo

data class TodoResponse(
    val id: Int,
    val description: String,
    val ownerId: Int,
    val completed: Boolean,
) {
    companion object {
        fun from(todo: Todo?) =
            todo?.let {
                TodoResponse(
                    it.id.value,
                    it.description,
                    it.ownerId,
                    it.completed,
                )
            }
    }
}
