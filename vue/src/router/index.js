import { createRouter, createWebHistory } from 'vue-router'
import RatingView from '@/views/rating/RatingView'
import RecommendedView from '@/views/recommended/RecommendedView'
import NotFound from '@/views/error/NotFound'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView'
import store from '@/store/index'

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/rating/",
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/user/account/register",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/rating/",
    name: "rating_index",
    component: RatingView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/recommended/",
    name: "recommended_index",
    component: RecommendedView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.requestAuth && !store.state.user.is_login) {
    next({ name: 'user_account_login'});
  }
  else {
    next();
  }
})

export default router
