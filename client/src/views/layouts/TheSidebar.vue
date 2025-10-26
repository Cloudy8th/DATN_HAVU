<script setup>
import { useCategoryStore } from "@/stores/category";
import { onMounted, ref } from "vue";

const categories = ref([]);

const categoryStore = useCategoryStore();

onMounted(async () => {
    await categoryStore.fecthGetAll();
    categories.value = categoryStore.categories;
});
</script>

<template>
    <nav id="sidebar">
        <div class="sidebar-title">
            <h3>DANH MỤC</h3>
        </div>
        <router-link
            :to="{ name: 'ProductCatalogry', params: { id: item?.id } }"
            v-for="item in categories?.data"
            :key="item?.id"
        >
            {{ item?.name }}
        </router-link>
    </nav>
</template>

<style scoped>
#sidebar {
    width: var(--width-sidebar);
    height: 100%;
    background-color: var(--color-white);
    overflow-y: auto;
    border-radius: var(--border-radius-page);
    padding: 10px 10px;
}

#sidebar .sidebar-title {
    padding: 10px;
    padding-bottom: 15px;
}

#sidebar a {
    padding: 10px 0;
    padding-left: 15px;
    display: block;
    text-decoration: none;
    color: var(--color-text);
    font-size: 1.1rem;
    border-radius: var(--border-radius-page);
}

#sidebar a:hover {
    background-color: var(--color-greyish);
}

#sidebar a.router-link-exact-active {
    background-color: var(--color-primary);
    color: var(--color-white);
}
</style>
