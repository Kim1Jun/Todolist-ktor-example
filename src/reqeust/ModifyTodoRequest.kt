package kim.wonjun.reqeust

data class ModifyTodoRequest(
    val description: String?,
    val weight: Int?,
    val completed: Boolean?,
)
