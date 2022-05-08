package net.macolabo.sform2.services.ApiToken.insert

case class ApiTokenInsertRequest(
                                group_name: String,
                                token: String,
                                expiry_days: Int
                                )
