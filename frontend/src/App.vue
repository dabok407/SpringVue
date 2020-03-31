<template>
  <form v-on:submit.prevent="saveForm">
    <div>
      <label for="username">id</label>
      <input type="text" id="username" v-model="username">
    </div>
    <div>
      <label for="password">pw</label>
      <input type="password" id="password" v-model="password">
    </div>
    <button type="submit">login</button>
  </form>
</template>

<script>

import axios from 'axios'

export default {
  data: function () {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    saveForm: function () {
      // event.preventDefault(); // 새로고침 방지
      // 상단form vue속성의 v-on:submit.prevent와 위의 preventDefault와 동일 기능
      console.log('username : ' + this.username + ' / password : ' + this.password)
      var url = 'https://jsonplaceholder.typicode.com/users'
      var data = {
        username: this.username,
        password: this.password
      }
      axios.post(url, data)
        .then(function (response) {
          console.log(response)
        })
        .catch(function (error) {
          console.log(error)
        })
    }
  }
}
</script>

<style>

</style>

<!--

<template>
  <div>
    <app-header
      v-bind:propsdata="str"
      v-bind:propsdata2="str2"
      v-on:renew="renewStr"
    >

    </app-header>
  </div>
</template>

<script>

    import AppHeader from './components/AppHeader.vue';

    export default {
        data: function () {
            return {
                str:'Header2'
                ,str2:'Header3'
            }
        },components: {
            'app-header': AppHeader
        },methods: {
            renewStr: function () {
                this.str = 'hi!';
            }
        }
    }
</script>

<style>
  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
  }
</style>
-->
