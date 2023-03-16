<template>
    <ContentField style="padding: 0 8vh;">
        <div class="row" style="padding: 0 1vh;">
            <h3 class="text-secondary">为您推荐以下电影</h3>
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
                                    <span class="text-secondary">推荐指数</span>
                                </div>
                                <div class="center">
                                    <span class="rate">{{ movie.value }}</span>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { ref } from 'vue'
import { useStore } from 'vuex';
import $ from 'jquery';

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();

        let movies = ref([]);

        const modifyPicture = () => {
            for (let i = 0; i < movies.value.length; i++) {
                movies.value[i].picture = 'https://' + movies.value[i].picture.substr(8).replaceAll('/', '//');
            }
        }

        const recommend = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/api/movie/recommend/",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    userId: store.state.user.id,
                    type: store.state.user.modify
                },
                success(resp) {
                    movies.value = resp;
                    modifyPicture();
                },
                error(resp) {
                    console.log(resp);
                }
            });
        }

        recommend();

        return {
            recommend,
            modifyPicture,
            movies
        }
    }
}
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