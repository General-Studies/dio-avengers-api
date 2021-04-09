package one.digitalinnovation.avengers.resource.avenger

import one.digitalinnovation.avengers.domain.avenger.Avenger
import javax.persistence.*

@Entity
@Table(name = "avenger")
data class AvengerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    @Column(nullable = false)
    val nick: String,
    @Column(nullable = false)
    val person: String,
    val description: String?,
    val history: String?
) {
    fun toAvenger() = Avenger(id, nick, person, description, history)

    companion object {
        fun from(avenger: Avenger) = AvengerEntity(
            id = avenger.id,
            nick = avenger.nick,
            description = avenger.description,
            history = avenger.history,
            person = avenger.person
        )
    }
}
