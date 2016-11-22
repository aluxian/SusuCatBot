package com.aluxian.susucatbot.config

trait SystemConfig {
  lazy val environment: Environment = Environment(sys.env.getOrElse("ENV", "development"))
  lazy val listenAddress: String = ":" + sys.env.getOrElse("PORT", 8888)
  lazy val mongoUrl: String = sys.env.getOrElse("MONGO_URI", "mongodb://localhost:27017/bot")
}

trait ServicesConfig {
  lazy val facebookAccessToken = sys.env("FACEBOOK_ACCESS_TOKEN")
  lazy val facebookVerifyToken = sys.env("FACEBOOK_VERIFY_TOKEN")
  lazy val facebookAppSecret = sys.env("FACEBOOK_APP_SECRET")
  lazy val facebookApiUrl = "https://graph.facebook.com/v2.8/"

  lazy val witAiAccessToken = sys.env("WITAI_ACCESS_TOKEN")
  lazy val witAiApiUrl = "https://api.wit.ai/"
  lazy val witAiApiVersion = "20160516"

  lazy val userAgent: String = "bot"
}

object Config extends SystemConfig with ServicesConfig

//this.port = process.env.PORT || '3000';
//this.mongoUrl = process.env.MONGO_URL || 'mongodb://test';
//this.userAgent = packageJson.name + '@' + packageJson.version;
//
//// Facebook
//this.facebookAppSecret = process.env.FACEBOOK_APP_SECRET;
//this.facebookApiUrl = ';
//
//// wit.ai
//this.witAiAccessToken = process.env.WITAI_ACCESS_TOKEN;
//this.witAiApiUrl = 'https://api.wit.ai';
//this.witAiApiVersion = '20160516';
//
//// Expedia
//this.expediaApiKey = process.env.EXPEDIA_API_KEY;
//this.expediaApiUrl = 'https://terminal2.expedia.com/x/activities/search';
//
//// other APIs
//this.forecastIoApiKey = process.env.FORECASTIO_API_KEY;
//this.googleMapsApiKey = process.env.GOOGLE_MAPS_API_KEY;
