package com.upsorok.user

case class Name(first: String, middle: String, last: String)

case class Email(prefix: String, domain: String) {
  override def toString(): String = s"${prefix}@${domain}"
}

object Email {
  def apply(email: String): Email = {
    val parts = email.split("@");
    if (parts.size != 2) {
      sys.error("email must have one and only one @ symbol")
    } else {
      Email(parts(0), parts(1))
    }
  }
}

case class Password(password: String) {
  override def toString(): String = "****"
}
