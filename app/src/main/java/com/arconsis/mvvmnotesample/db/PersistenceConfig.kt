package com.arconsis.mvvmnotesample.db

import org.droitateddb.config.Persistence

/**
 * Created by Alexander on 05.05.2017.
 */
@org.droitateddb.config.Persistence(dbVersion = 1, dbName = "notes.db")
open class PersistenceConfig