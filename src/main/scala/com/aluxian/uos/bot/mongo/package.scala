package com.aluxian.uos.bot

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

package object mongo {
  def findOneOrCreate[T](collection: BSONCollection)
                        (userQuery: BSONDocument, getNewDoc: => Future[T]): Future[T] = {
    val p = Promise[T]()

    collection.find(userQuery).one[T].onComplete {
      case Success(docOpt) =>
        if (docOpt.isDefined) {
          p.complete(Try(docOpt.get))
        } else {
          getNewDoc.onComplete {
            case Success(newDocOpt) =>
              collection.insert(newDocOpt).onComplete { r =>
                collection.find(userQuery).one[T]
                  .onComplete {
                    case Success(docOpt2) => p.complete(Try(docOpt2.get))
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
