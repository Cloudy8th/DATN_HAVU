<template>
  <TheHeader />
  <main class="grid">
    <section>
      <router-view></router-view>
    </section>
  </main>
  <TheFooter />
  <ChatBot />
</template>

<script>
import TheHeader from "./TheHeader.vue";
import TheFooter from "./TheFooter.vue";
import ChatBot from "../pages/chatbot/ChatBot.vue";

export default {
  name: "TheContainer",
  components: {
    TheHeader,
    TheFooter,
    ChatBot,
  },
};
</script>

<style scoped>
main {
  width: 100%;
  display: flex;
  background-color: var(--bg-main);
  padding: 20px 40px;
  height: 100%;
}

section {
  width: 100%;
  min-height: calc(100vh - var(--height-header) - var(--height-footer) - 20px);
}
</style>
