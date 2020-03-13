<template>
  <header>
    <h1>{{propsdata}} / {{propsdata2}}</h1>
    <button v-on:click="sendEvent">클릭</button>
  </header>
</template>

<script>
    export default {
        props: ['propsdata', 'propsdata2']
        ,methods: {
            sendEvent: function () {
                this.$emit('renew');
            }
        }
    }
</script>

<style scoped>

</style>
