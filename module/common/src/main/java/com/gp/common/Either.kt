package com.gp.common

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.identity

/**
 * Same as Either.left
 */
fun <A> A.success(): Either<A, Nothing> = Left(this)

/**
 * Same as Either.right
 */
fun <A> A.failure(): Either<Nothing, A> = Right(this)

/**
 * Same as [Either.leftOrNull].
 */
fun <A, B> Either<A, B>.successOrNull(): A? = fold(::identity) { null }