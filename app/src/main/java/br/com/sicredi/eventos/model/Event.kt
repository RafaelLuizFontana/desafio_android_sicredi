package br.com.sicredi.eventos.model

data class Event (
    val people: List<Person>?,
    val date: Long?,
    val description: String?,
    val image: String?,
    val longitude: Double?,
    val latitude: Double?,
    val price: Double?,
    val title: String?,
    val id: String?
) {

}