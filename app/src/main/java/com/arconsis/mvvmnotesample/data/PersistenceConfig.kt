package com.arconsis.mvvmnotesample.data

import org.droitateddb.config.Persistence

/**
 * Created by Alexander on 05.05.2017.
 */
@Persistence(dbVersion = 1, dbName = "notes.db")
open class PersistenceConfig