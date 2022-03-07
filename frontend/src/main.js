/*!

=========================================================
* Vue Argon Design System - v1.1.0
=========================================================

* Product Page: https://www.creative-tim.com/product/argon-design-system
* Copyright 2019 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://github.com/creativetimofficial/argon-design-system/blob/master/LICENSE.md)

* Coded by www.creative-tim.com

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/
import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import Argon from "./plugins/argon-kit";
import axios from 'axios'
import moment from 'vue-moment'
import './registerServiceWorker'
// import * as VueGoogleMaps from "vue2-google-maps";



import 'vue-multiselect/dist/vue-multiselect.min.css'
import "vue2-datepicker/index.css";
import * as VueGoogleMaps from "vue2-google-maps";

Vue.use(VueGoogleMaps, {
  load: {
    key: "AIzaSyCPe9MVNFIUhRwnQExIPZoIuA02a-o9gkU",
    libraries: "places", // This is required if you use the Autocomplete plugin
    // OR: libraries: 'places,drawing'
    // OR: libraries: 'places,drawing,visualization'
    // (as you require)

    //// If you want to set the version, you can do so:
    // v: '3.26',
  },

  installComponents: true,
});

Vue.config.productionTip = false;
Vue.use(Argon, axios, moment);
new Vue({
  router,
  render: h => h(App)
}).$mount("#app");
