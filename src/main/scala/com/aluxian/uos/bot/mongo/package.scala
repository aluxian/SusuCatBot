package com.aluxian.uos.bot

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

package object mongo {
  def findOneOrCreate(collection: BSONCollection)
                     (userQuery: BSONDocument, getNewDoc: => Future[MongoUser]): Future[MongoUser] = {

    implicit def mongoEntityWriter: BSONDocumentWriter[MongoEntity] = Macros.writer[MongoEntity]
    implicit def mongoThoughtWriter: BSONDocumentWriter[MongoThought] = Macros.writer[MongoThought]
    implicit def mongoMessageWriter: BSONDocumentWriter[MongoMessage] = Macros.writer[MongoMessage]
    implicit def mongoUserWriter: BSONDocumentWriter[MongoUser] = Macros.writer[MongoUser]

    implicit def mongoEntityReader: BSONDocumentReader[MongoEntity] = Macros.reader[MongoEntity]
    implicit def mongoThoughtReader: BSONDocumentReader[MongoThought] = Macros.reader[MongoThought]
    implicit def mongoMessageReader: BSONDocumentReader[MongoMessage] = Macros.reader[MongoMessage]
    implicit def mongoUserReader: BSONDocumentReader[MongoUser] = Macros.reader[MongoUser]

    val p = Promise[MongoUser]()

    collection.find(userQuery).one[MongoUser].onComplete {
      case Success(docOpt) =>
        if (docOpt.isDefined) {
          p.complete(Try(docOpt.get))
        } else {
          getNewDoc.onComplete {
            case Success(newDocOpt) =>
              collection.insert(newDocOpt).onComplete { r =>
                collection.find(userQuery).one[MongoUser]
                  .onComplete {
                    case Success(docOpt2) =>
                      val mongoUser = docOpt2.get
                      p.complete(Try(mongoUser))
                    case Failure(ex) => p.failure(ex)
                  }
              }
            case Failure(ex) => p.failure(ex)
          }
        }
      case Failure(ex) => p.failure(ex)
    }

    p.future
  }
}
