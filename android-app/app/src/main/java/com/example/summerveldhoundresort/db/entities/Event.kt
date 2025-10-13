package com.example.summerveldhoundresort.db.entities

class Event {

    var id: String = ""
    var date: String = ""
    var description: String= ""
    var location: String= ""
    var name: String= ""
    var time: String= ""

    // Firestore requires an explicit no-arg constructor
    constructor()

    constructor(id:String, name: String, description: String, date: String, location: String, time: String) {
        this.id = id
        this.name = name
        this.description = description
        this.date = date
        this.location = location
        this.time = time
    }
}
