package com.arconsis.mvvmnotesample.db

import org.droitateddb.entity.Column
import org.droitateddb.entity.Entity
import org.droitateddb.entity.PrimaryKey

@Entity
data class NoteDb(
        @PrimaryKey
        @Column
        var id: Int?,
        @Column
        var title: String,
        @Column
        var message: String,
        @Column
        var userId: Int) {
    constructor() : this(-1, "", "", -1)
}