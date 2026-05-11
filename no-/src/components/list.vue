<template>
    <v-list lines="one">
  <v-list-item
    v-for="n in 3"
    :key="n"
    :title="'Item ' + n"
    subtitle="Lorem ipsum dolor sit amet consectetur adipisicing elit"
  ></v-list-item>
</v-list>
</template>
<script setup>
</script>