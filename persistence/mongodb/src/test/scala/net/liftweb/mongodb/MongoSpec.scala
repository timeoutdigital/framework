/*
 * Copyright 2014 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftweb
package mongodb

import org.specs2.mutable.Specification
import org.specs2.execute.Result

import com.mongodb._

class MongoSpec extends Specification  {
  "Mongo Specification".title

  case object TestMongoIdentifier extends MongoIdentifier {
    val jndiName = "test_a"
  }

  def passDefinitionTests(id: MongoIdentifier, mc: Mongo, db: String): Result = {
    // define the db
    //MongoDB.closeAll()
    MongoDB.defineDb(id, mc, db)

    // make sure mongo is running
    try {
      MongoDB.use(id) { db =>
        db.getLastError.ok must beEqualTo(true)
      }
    }
    catch {
      case e: Exception => skipped("MongoDB is not running")
    }

    // using an undefined identifier throws an exception
    MongoDB.use(DefaultMongoIdentifier) { db =>
      db.getLastError.ok must beEqualTo(true)
    } must throwA(new MongoException("Mongo not found: MongoIdentifier(test)"))
    // remove defined db
    MongoDB.closeAll()
    success
  }

  "Mongo" should {

    "Define DB with Mongo instance" in {
      passDefinitionTests(TestMongoIdentifier, new Mongo, "test_default_a")
    }

    "Define DB with MongoClient instance" in {
      val opts = MongoClientOptions.builder
        .connectionsPerHost(12)
        .build
      passDefinitionTests(TestMongoIdentifier, new MongoClient(new ServerAddress("localhost"), opts), "test_default_b")
    }

    /* Requires a server other than localhost with auth setup.
    "Define and authenticate DB with Mongo instance" in {
      MongoDB.close

      // make sure mongo is running
      try {
        val pwd = "lift_pwd"
        val dbUri = new MongoURI("mongodb://")
        // define the db
        MongoDB.defineDbAuth(TestMongoIdentifier, new Mongo(dbUri), "lift_auth_test", "lift_user", pwd)
        // try to use it
        MongoDB.use(TestMongoIdentifier) { db =>
          db.getLastError.ok must beEqualTo(true)
        }
      }
      catch {
        case e: Exception => skip("MongoDB is not running")
      }
      // remove defined db
      MongoDB.closeAll()
    }
    */
  }
}
