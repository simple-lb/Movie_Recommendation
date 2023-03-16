<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                <h1 class="h3 mb-5 fw-normal">Please sign in</h1>

                <div class="form-floating">
                    <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    <label for="username">用户名</label>
                </div>
                <div class="form-floating">
                    <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    <label for="password">密码</label>
                </div>

                <div class="error-message">{{ error_message }}</div>
                <button class="w-100 btn btn-lg btn-primary" type="submit">登录</button>
            </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';
import router from '@/router/index';

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');
        
        const jwt_token = localStorage.getItem("jwt_token");
        if (jwt_token) {
            store.commit("updateToken", jwt_token);
            store.dispatch("getinfo", {
                success() {
                    router.push({ name: 'rating_index'});
                    store.commit("updatePullingInfo", false);
                },
                error() {
                    store.commit("updatePullingInfo", false);
                }
            })
        } else {
            store.commit("updatePullingInfo", false);
        }
        

        const login = () => {
            error_message.value = '';
            store.dispatch("login", {
                username: username.value,
                password: password.value,
                success() {
                    store.dispatch("getinfo", {
                        success() {
                            router.push({ name: 'rating_index' });
                        }
                    })
                },
                error() {
                    error_message.value = '用户名或密码错误';
                }
            })
        }

        return {
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>

<style scoped>
button {
    margin-top: 50px;
}
div.error-message {
    color: red;
}
</style>