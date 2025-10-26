<script setup>
import { nextTick, watch } from "vue";
import { useUserStore } from "./stores/user";
import { useAuthStore } from "./stores/auth";

const userSotore = useUserStore();
const authStore = useAuthStore();

watch(
    () => authStore.isLoggedIn,
    () => {
        nextTick(async () => {
            await userSotore.fetchGetById();
        });
    }
);
</script>

<template>
    <router-view :key="$route.fullPath"></router-view>
</template>

<style src="./styles/style.css"></style>
