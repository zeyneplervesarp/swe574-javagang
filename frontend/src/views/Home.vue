<template>
  <div>
    <!-- <hero></hero> -->
    <div class="position-relative">
      <!-- shape Hero -->
      <section class="section-shaped my-0">
        <div class="shape shape-style-2 shape-default shape-skew">
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
        </div>
        <div class="container shape-container d-flex">
          <div class="row">
              <div class="col-md-4">
                <h1 class="display-3 text-white">
                  Welcome!
                  <span>Everything is fine.</span>
                </h1>
                <p class="lead text-white">
                  Welcome to social hub where you can offer your services, join
                  other services and be part of a growing community where time
                  is the only currency. Here are a few services.
                </p>
                <div class="btn-wrapper">
                  <router-link
                    v-if="!userLoggedIn"
                    to="/login"
                    class="btn btn-success"
                  >
                    Login Page
                  </router-link>
                  <router-link
                    v-if="!userLoggedIn"
                    to="/register"
                    class="btn btn-success"
                  >
                    Register Page
                  </router-link>
                </div>
              </div>
               <div class="col-md-8" v-if="userLoggedIn">
                 <section class="section section-lg pt-lg-0 mt--200">
                    <recommendations></recommendations>
                </section>
               </div>
               <div class="col-md-8" v-if="!userLoggedIn">
                <section class="section section-lg pt-lg-0 mt--300">
                   <services  :filter="'featured'"></services>
                 </section>
               </div>
            </div>
         
        </div>
      </section>
      <!-- 1st Hero Variation -->
    </div>
    
    <section class="section section-lg pt-lg-0 mt--300"  v-if="userLoggedIn">
      <services  :filter="'featured'"></services>
    </section>
    
    <section class="section section-lg pt-0">
      <div class="container">
        <card gradient="primary" no-body shadow-size="lg" class="border-0">
          <div class="p-5">
            <div class="row align-items-center">
              <div class="col-lg-8">
                <h3 class="text-white">
                  Want to take a look at all services?
                </h3>
                <p class="lead text-white mt-3">
                  Explore all sevices offered by the socialhub community!
                </p>
              </div>
              <div class="col-lg-3 ml-lg-auto">
                <base-button
                  tag="a"
                  href="#/allServices"
                  type="white"
                  block
                  size="lg"
                >
                  Click to Browse
                </base-button>
              </div>
            </div>
          </div>
        </card>
      </div>
    </section>
    </div>
</template>

<script>
import Hero from "./components/Hero";
import Recommendations from './components/Recommendations.vue';
import Services from "./components/Services.vue";

export default {
  name: "home",
  components: {
    Hero,
    Services,
    Recommendations,
  },
  data() {
    return {
      userLoggedIn: "",
    };
  },
  mounted() {
    let token = JSON.parse(localStorage.getItem("token"));

    if (token) {
      this.userLoggedIn = true;
    } else {
      this.userLoggedIn = false;
    }
  },
};
</script>
