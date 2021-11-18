package net.macolabo.sform2.utils.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import net.macolabo.sform2.models.entity.user.User

/**
 * The default env.
 */
trait DefaultEnv extends Env {
//  type I = User
//  type A = JWTAuthenticator
}
