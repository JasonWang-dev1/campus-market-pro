<template>
  <router-view v-slot="{ Component }">
    <transition name="page" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
/* Page transition */
.page-enter-active {
  animation: page-in 0.25s ease-out;
}
.page-leave-active {
  animation: page-out 0.2s ease-in;
}
@keyframes page-in {
  0%   { opacity: 0; transform: translateY(8px); }
  100% { opacity: 1; transform: translateY(0); }
}
@keyframes page-out {
  0%   { opacity: 1; transform: translateY(0); }
  100% { opacity: 0; transform: translateY(-4px); }
}
</style>
