package scalaz

import java.util.AbstractMap.SimpleImmutableEntry

trait Applic[F[_]] {
  def applic[A, B](f: F[A => B]): F[A] => F[B]

  def **[G[_] : Applic]: Applic[({type λ[α] = (F[α], G[α])})#λ] =
    new Applic[({type λ[α] = (F[α], G[α])})#λ] {
      def applic[A, B](f: (F[A => B], G[A => B])) = {
        case (a, b) => (Applic.this.applic(f._1)(a), implicitly[Applic[G]].applic(f._2)(b))
      }
    }

  def deriving[G[_]](implicit n: ^**^[G, F]): Applic[G] =
    new Applic[G] {
      def applic[A, B](f: G[A => B]) =
        k => n.pack(Applic.this.applic(n unpack f)(n unpack k))
    }
}

object Applic extends Applics

trait Applics {

  implicit val OptionApplic: Applic[Option] = new Applic[Option] {
    def applic[A, B](f: Option[A => B]) =
      a => f flatMap (a map _)
  }

  implicit val ListApplic: Applic[List] = new Applic[List] {
    def applic[A, B](f: List[A => B]) =
      a => f flatMap (a map _)
  }

  implicit val StreamApplic: Applic[Stream] = new Applic[Stream] {
    def applic[A, B](f: Stream[A => B]) =
      a => f flatMap (a map _)
  }

  implicit def EitherLeftApplic[X]: Applic[({type λ[α]=Either.LeftProjection[α, X]})#λ] =
    new Applic[({type λ[α]=Either.LeftProjection[α, X]})#λ] {
      def applic[A, B](f: Either.LeftProjection[A => B, X]) =
      a => f flatMap (g => (a map g)) left
    }

  implicit def EitherRightApplic[X]: Applic[({type λ[α]=Either.RightProjection[X, α]})#λ] =
    new Applic[({type λ[α]=Either.RightProjection[X, α]})#λ] {
      def applic[A, B](f: Either.RightProjection[X, A => B]) =
        a => f flatMap (a map _) right
    }

  implicit def EitherApplic[X]: Applic[({type λ[α]=Either[X, α]})#λ] =
    new Applic[({type λ[α]=Either[X, α]})#λ] {
      def applic[A, B](f: Either[X, A => B]) =
        a => f.right flatMap (a.right map _)
    }

  import java.util.Map.Entry

  implicit def MapEntryApply[X: Semigroup]: Applic[({type λ[α]=Entry[X, α]})#λ] =
    new Applic[({type λ[α]=Entry[X, α]})#λ] {
      def applic[A, B](f: Entry[X, A => B]) =
        e => new SimpleImmutableEntry[X, B](implicitly[Semigroup[X]].append(f.getKey, e.getKey), f.getValue.apply(e.getValue))

    }

  implicit def Tuple1Applic: Applic[Tuple1] = new Applic[Tuple1] {
    def applic[A, B](f: Tuple1[A => B]) =
      a => Tuple1(f._1(a._1))
  }

  implicit def Tuple2Applic[R: Semigroup]: Applic[({type λ[α]=(R, α)})#λ] = new Applic[({type λ[α]=(R, α)})#λ] {
    def applic[A, B](f: (R, A => B)) = {
      case (r, a) => (implicitly[Semigroup[R]].append(f._1, r), f._2(a))
    }
  }

  implicit def Tuple3Applic[R: Semigroup, S: Semigroup]: Applic[({type λ[α]=(R, S, α)})#λ] = new Applic[({type λ[α]=(R, S, α)})#λ] {
    def applic[A, B](f: (R, S, A => B)) = {
      case (r, s, a) => (implicitly[Semigroup[R]].append(f._1, r), implicitly[Semigroup[S]].append(f._2, s), f._3(a))
    }
  }

  implicit def Tuple4Applic[R: Semigroup, S: Semigroup, T: Semigroup]: Applic[({type λ[α]=(R, S, T, α)})#λ] = new Applic[({type λ[α]=(R, S, T, α)})#λ] {
    def applic[A, B](f: (R, S, T, A => B)) = {
      case (r, s, t, a) => (implicitly[Semigroup[R]].append(f._1, r), implicitly[Semigroup[S]].append(f._2, s), implicitly[Semigroup[T]].append(f._3, t), f._4(a))
    }
  }

  implicit def Tuple5Applic[R: Semigroup, S: Semigroup, T: Semigroup, U: Semigroup]: Applic[({type λ[α]=(R, S, T, U, α)})#λ] = new Applic[({type λ[α]=(R, S, T, U, α)})#λ] {
    def applic[A, B](f: (R, S, T, U, A => B)) = {
      case (r, s, t, u, a) => (implicitly[Semigroup[R]].append(f._1, r), implicitly[Semigroup[S]].append(f._2, s), implicitly[Semigroup[T]].append(f._3, t), implicitly[Semigroup[U]].append(f._4, u), f._5(a))
    }
  }

  implicit def Tuple6Applic[R: Semigroup, S: Semigroup, T: Semigroup, U: Semigroup, V: Semigroup]: Applic[({type λ[α]=(R, S, T, U, V, α)})#λ] = new Applic[({type λ[α]=(R, S, T, U, V, α)})#λ] {
    def applic[A, B](f: (R, S, T, U, V, A => B)) = {
      case (r, s, t, u, v, a) => (implicitly[Semigroup[R]].append(f._1, r), implicitly[Semigroup[S]].append(f._2, s), implicitly[Semigroup[T]].append(f._3, t), implicitly[Semigroup[U]].append(f._4, u), implicitly[Semigroup[V]].append(f._5, v), f._6(a))
    }
  }

  implicit def Tuple7Applic[R: Semigroup, S: Semigroup, T: Semigroup, U: Semigroup, V: Semigroup, W: Semigroup]: Applic[({type λ[α]=(R, S, T, U, V, W, α)})#λ] = new Applic[({type λ[α]=(R, S, T, U, V, W, α)})#λ] {
    def applic[A, B](f: (R, S, T, U, V, W, A => B)) = {
      case (r, s, t, u, v, w, a) => (implicitly[Semigroup[R]].append(f._1, r), implicitly[Semigroup[S]].append(f._2, s), implicitly[Semigroup[T]].append(f._3, t), implicitly[Semigroup[U]].append(f._4, u), implicitly[Semigroup[V]].append(f._5, v), implicitly[Semigroup[W]].append(f._6, w), f._7(a))
    }
  }
    
  implicit def Function0Applic: Applic[Function0] = new Applic[Function0] {
    def applic[A, B](f: Function0[A => B]) =
      a => () => f.apply.apply(a.apply)
  }

  implicit def Function1Applic[R]: Applic[({type λ[α]=(R) => α})#λ] = new Applic[({type λ[α]=(R) => α})#λ] {
    def applic[A, B](f: Function1[R, A => B]) =
      a => r => f(r)(a(r))
  }

  implicit def Function2Applic[R, S]: Applic[({type λ[α]=(R, S) => α})#λ] = new Applic[({type λ[α]=(R, S) => α})#λ] {
    def applic[A, B](f: Function2[R, S, A => B]) =
      a => (r, s) => f(r, s)(a(r, s))
  }

  implicit def Function3Applic[R, S, T]: Applic[({type λ[α]=(R, S, T) => α})#λ] = new Applic[({type λ[α]=(R, S, T) => α})#λ] {
    def applic[A, B](f: Function3[R, S, T, A => B]) =
      a => (r, s, t) => f(r, s, t)(a(r, s, t))
  }

  implicit def Function4Applic[R, S, T, U]: Applic[({type λ[α]=(R, S, T, U) => α})#λ] = new Applic[({type λ[α]=(R, S, T, U) => α})#λ] {
    def applic[A, B](f: Function4[R, S, T, U, A => B]) =
      a => (r, s, t, u) => f(r, s, t, u)(a(r, s, t, u))
  }

  implicit def Function5Applic[R, S, T, U, V]: Applic[({type λ[α]=(R, S, T, U, V) => α})#λ] = new Applic[({type λ[α]=(R, S, T, U, V) => α})#λ] {
    def applic[A, B](f: Function5[R, S, T, U, V, A => B]) =
      a => (r, s, t, u, v) => f(r, s, t, u, v)(a(r, s, t, u, v))
  }

  implicit def Function6Applic[R, S, T, U, V, W]: Applic[({type λ[α]=(R, S, T, U, V, W) => α})#λ] = new Applic[({type λ[α]=(R, S, T, U, V, W) => α})#λ] {
    def applic[A, B](f: Function6[R, S, T, U, V, W, A => B]) =
      a => (r, s, t, u, v, w) => f(r, s, t, u, v, w)(a(r, s, t, u, v, w))
  }

  implicit val IdentityApplic: Applic[Identity] = implicitly[Monad[Identity]].applic

  implicit def KleisliApplic[F[_], R](implicit ap: Applic[F]): Applic[({type λ[α] = Kleisli[R, F, α]})#λ] = new Applic[({type λ[α] = Kleisli[R, F, α]})#λ] {
    def applic[A, B](f: Kleisli[R, F, A => B]) =
      a => Kleisli.kleisli(r =>
        ap.applic(f.run(r))(a.run(r)))
  }

  implicit val NonEmptyListApplic: Applic[NonEmptyList] = new Applic[NonEmptyList] {
    def applic[A, B](f: NonEmptyList[A => B]) =
      r =>
        for {
          ff <- f
          rr <- r
        } yield ff(rr)
  }

  implicit def StateTApplic[A, F[_] : Monad]: Applic[({type λ[α] = StateT[A, F, α]})#λ] = new Applic[({type λ[α] = StateT[A, F, α]})#λ] {
    def applic[X, Y](f: StateT[A, F, X => Y]) =
      implicitly[Monad[({type λ[α] = StateT[A, F, α]})#λ]].liftM2[X => Y, X, Y](identity)(f)
  }

  implicit def StepListTApplic[F[_] : Functor]: Applic[({type λ[X] = StepListT[F, X]})#λ] = new Applic[({type λ[X] = StepListT[F, X]})#λ] {
    def applic[A, B](f: StepListT[F, A => B]) =
      a =>
        for {
          ff <- f
          aa <- a
        } yield ff(aa)
  }

  implicit def StepStreamTApplic[F[_] : Functor]: Applic[({type λ[X] = StepStreamT[F, X]})#λ] = new Applic[({type λ[X] = StepStreamT[F, X]})#λ] {
    def applic[A, B](f: StepStreamT[F, A => B]) =
      a =>
        for {
          ff <- f
          aa <- a
        } yield ff(aa)
  }

  implicit val TreeApplic: Applic[Tree] = new Applic[Tree] {
    import wrap.StreamW._
    def applic[A, B](f: Tree[A => B]) =
      a =>
        Tree.node((f.rootLabel)(a.rootLabel), implicitly[Applic[newtypes.ZipStream]].applic(f.subForest.map(applic[A, B](_)).ʐ)(a.subForest ʐ).value)
  }

  implicit def FailProjectionApplic[X]: Applic[({type λ[α] = FailProjection[α, X]})#λ] =
    new Applic[({type λ[α] = FailProjection[α, X]})#λ] {
      def applic[A, B](f: FailProjection[A => B, X]) =
        a =>
          ((f.validation, a.validation) match {
            case (Success(x1), Success(_)) => Success[B, X](x1)
            case (Success(x1), Failure(_)) => Success[B, X](x1)
            case (Failure(_), Success(x2)) => Success[B, X](x2)
            case (Failure(f), Failure(e))  => Failure[B, X](f(e))
          }).fail
    }

  implicit def ValidationApplic[X: Semigroup]: Applic[({type λ[α] = Validation[X, α]})#λ] = new Applic[({type λ[α] = Validation[X, α]})#λ] {
    def applic[A, B](f: Validation[X, A => B]) =
      a => (f, a) match {
        case (Success(f), Success(a)) => Validation.success(f(a))
        case (Success(_), Failure(e)) => Validation.failure(e)
        case (Failure(e), Success(_)) => Validation.failure(e)
        case (Failure(e1), Failure(e2)) => Validation.failure(implicitly[Semigroup[X]].append(e1, e2))
      }
  }

  implicit def WriterTApplic[A: Semigroup, F[_] : ApplicFunctor]: Applic[({type λ[α] = WriterT[A, F, α]})#λ] = new Applic[({type λ[α] = WriterT[A, F, α]})#λ] {
    def applic[X, Y](f: WriterT[A, F, X => Y]) =
      a =>
        WriterT.writerT(implicitly[ApplicFunctor[F]].liftA2((ff: (A, X => Y)) => (xx: (A, X)) => (implicitly[Semigroup[A]].append(ff._1, xx._1), ff._2(xx._2)))(f.runT)(a.runT))
  }

}