<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container">
            <router-link class="navbar-brand" :to="{ name: 'home' }">电影推荐</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText"
                aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <router-link :class="route_name == 'rating_index' ? 'nav-link active' : 'nav-link'" :to="{ name: 'rating_index' }">评分</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="route_name == 'recommended_index' ? 'nav-link active' : 'nav-link'" :to="{ name: 'recommended_index' }">推荐</router-link>
                    </li>
                </ul>
                
                <ul class="navbar-nav" style="margin-right: 5%;" v-if="$store.state.user.is_login">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                            data-bs-toggle="dropdown" aria-expanded="false">
                            {{ $store.state.user.username }}
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <li>
                                <a class="dropdown-item" href="#" @click="logout">退出</a>
                            </li>
                        </ul>
                    </li>
                </ul>
                <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
                    <li class="nav-item">
                        <router-link class="nav-link" :to="{ name: 'user_account_login' }" role="button">
                            登录
                        </router-link>
                    </li>
                    <li class="nav-item">
                        <router-link class="nav-link" :to="{ name: 'user_account_register' }" role="button">
                            注册
                        </router-link>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>

<script>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useStore } from 'vuex'


export default {
    setup() {
        const store = useStore();
        const route = useRoute();
        const logout = () => {
            store.dispatch("logout");
        }
        let route_name = computed(() => route.name);
        return {
            route_name,
            logout
        };
    }
}
</script>

<style scoped>

</style>