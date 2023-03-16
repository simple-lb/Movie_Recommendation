<template>
  <ContentField style="padding: 0 8vh;">
    <div class="row" style="padding: 0 1vh;">
      <div class="col-4">
        <input v-model="movieName" type="text" class="form-control" id="movieName" placeholder="请输入电影名称">
      </div>
      <div class="col-1">
        <button type="button" class="btn btn-light" @click="search">搜索</button>
      </div>
    </div>
    <div class="row" style="padding: 5vh 0;">
      <div class="col-11">
        <table class="table">
          <tbody>
            <tr v-for="(movie, index) in movies" :key="index">
              <td style="width: 15vh;"><img :src=movie.picture class="rounded float-start img-fluid"></td>
              <td class="float start">
                <div>
                  <h1 class="title">{{ movie.title }}({{ movie.year }})</h1>
                  <div style="margin-top: 3vh;">
                    <span class="rate">{{ movie.rate }}分</span>
                  </div>
                  <div class="text-secondary">
                    <span>{{ movie.desc }}</span>
                  </div>
                </div>
              </td>
              <td>
                <div class="center">
                  <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#movie-rating"
                    @click="setIndex(index)">评分</button>
                </div>
                <!-- Modal -->
                <div class="modal fade" id="movie-rating" tabindex="-1">
                  <div class="modal-dialog">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h1 class="modal-title fs-5">评分</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                      </div>
                      <div class="modal-body">
                        <input v-model="movieRating" type="text" class="form-control" id="movieRating"
                          placeholder="请输入分数">
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-primary" @click="rating(cnt)"
                          data-bs-dismiss="modal">提交</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="center">
                  <span class="rate">{{ movie.myRating }}</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="col-1">
        <button type="button" class="btn btn-outline-primary" @click="change">换一批</button>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField.vue";
import { ref } from "vue";
import { useStore } from "vuex";
import $ from "jquery";
import router from "@/router/index";

export default {
  components: {
    ContentField,
  },
  setup() {
    const store = useStore();
    store.commit("updateModify", false);

    const jwt_token = localStorage.getItem("jwt_token");
    if (jwt_token) {
      store.commit("updateToken", jwt_token);
      store.dispatch("getinfo", {
        success() {
          router.push({ name: "rating_index" });
          store.commit("updatePullingInfo", false);
        },
        error() {
          store.commit("updatePullingInfo", false);
        },
      });
    } else {
      store.commit("updatePullingInfo", false);
    }

    let movieName = ref("");
    let movieRating = ref("");
    let movies = ref([]);
    let cnt = ref("");

    const search = () => {
      if (movieName.value != "") {
        $.ajax({
          url: "http://127.0.0.1:3000/api/movie/search/",
          type: "post",
          data: {
            title: movieName.value,
          },
          success(resp) {
            movies.value = resp;
            modifyPicture();
            getCurMovieRating();
          },
        });
      }
    };

    const rating = (i) => {
      if (movieRating.value != "") {
        store.commit("updateModify", true);
        $.ajax({
          url: "http://127.0.0.1:3000/api/movie/rate/",
          type: "post",
          headers: {
            Authorization: "Bearer " + store.state.user.token,
          },
          data: {
            movieId: movies.value[i].id,
            userId: store.state.user.id,
            rate: movieRating.value,
          },
          success(resp) {
            getCurMovieRating();
            console.log(resp);
          },
          error(resp) {
            console.log(resp);
          },
        });
      }
    };

    const setIndex = (i) => {
      cnt.value = i;
    };

    const change = () => {
      $.ajax({
        url: "http://127.0.0.1:3000/api/movie/change/",
        type: "get",
        success(resp) {
          movies.value = resp;
          modifyPicture();
          getCurMovieRating();
        },
        error(resp) {
          console.log(resp);
        },
      });
    };

    const modifyPicture = () => {
      for (let i = 0; i < movies.value.length; i++) {
        movies.value[i].picture =
          "https://" + movies.value[i].picture.substr(8).replaceAll("/", "//");
      }
    };

    change();

    let allMovieRating = [];

    const getCurMovieRating = () => {
      console.log(store.state.user.id);
      $.ajax({
        url: "http://127.0.0.1:3000/api/movie/getRating/",
        type: "post",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          userId: store.state.user.id,
        },
        success(resp) {
          allMovieRating = resp;
          updateMyRating();
          console.log(allMovieRating);
        },
        error(resp) {
          console.log(resp);
        },
      });
    };

    getCurMovieRating();

    const updateMyRating = () => {
      for (let i = 0; i < movies.value.length; i++) {
        movies.value[i].myRating = curMovieRating(movies.value[i].id);
      }
    };

    let curMovieRating = (movieId) => {
      let res = "未评分";
      allMovieRating.forEach((element) => {
        console.log(element.movieId + "---------" + movieId);
        if (element.movieId === movieId) {
          res = element.rate + "分";
          return;
        }
      });
      return res;
    };

    return {
      movieName,
      movieRating,
      search,
      rating,
      change,
      setIndex,
      movies,
      cnt,
      modifyPicture,
      curMovieRating,
      getCurMovieRating
    };
  },
};
</script>

<style scoped>
img {
  width: 10vh;
}

h1.title {
  font-size: 3vh;
}

.rate {
  font-size: 3vh;
  color: #ff9900;
}

.center {
  padding: 0.9vh 0;
  text-align: center;
}
</style>