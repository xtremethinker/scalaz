package scalaz

trait Join[F[_]] {
  def join[A]: F[F[A]] => F[A]
}

object Join extends Joins

trait Joins {
  implicit val OptionJoin: Join[Option] = new Join[Option] {
    def join[A] =
      _ flatMap identity
  }

  implicit val ListJoin: Join[List] = new Join[List] {
    def join[A] =
      _ flatMap identity
  }

  implicit val StreamJoin: Join[Stream] = new Join[Stream] {
    def join[A] =
      _ flatMap identity
  }

  implicit val IdentityJoin: Join[Identity] = implicitly[Monad[Identity]].join

  implicit def KleisliJoin[F[_], R](implicit bd: Bind[F]): Join[({type λ[α] = Kleisli[R, F, α]})#λ] = new Join[({type λ[α] = Kleisli[R, F, α]})#λ] {
    def join[A] =
      _ flatMap (z => z)
  }

  implicit val NonEmptyListJoin: Join[NonEmptyList] = new Join[NonEmptyList] {
    def join[A] =
      _ flatMap (z => z)
  }

  implicit def StateTJoin[A, F[_] : Bind]: Join[({type λ[α] = StateT[A, F, α]})#λ] = new Join[({type λ[α] = StateT[A, F, α]})#λ] {
    def join[A] =
      _ flatMap (z => z)
  }

  implicit def StepListTJoin[F[_] : Functor]: Join[({type λ[X] = StepListT[F, X]})#λ] = new Join[({type λ[X] = StepListT[F, X]})#λ] {
    def join[A] =
      _ flatMap (z => z)
  }

  implicit def StepStreamTJoin[F[_] : Functor]: Join[({type λ[X] = StepStreamT[F, X]})#λ] = new Join[({type λ[X] = StepStreamT[F, X]})#λ] {
    def join[A] =
      _ flatMap (z => z)
  }

  implicit def WriterTJoin[A: Semigroup, F[_] : BindFunctor]: Join[({type λ[α] = WriterT[A, F, α]})#λ] = new Join[({type λ[α] = WriterT[A, F, α]})#λ] {
    def join[A] =
      _ flatMap (z => z)
  }

}