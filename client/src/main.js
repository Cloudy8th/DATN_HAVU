import { createApp } from 'vue'
import App from './App.vue'
import router from './routers/router';
import { createPinia } from 'pinia';

const pinia = createPinia()
const app = createApp(App);

// Components
import BaseButton from '@/components/button/BaseButton.vue';
import BaseInput from '@/components/input/BaseInput.vue';
import BaseDialog from '@/components/dialog/BaseDialog.vue';
import BaseRating from '@/components/rating/BaseRating.vue';
import BaseCheckbox from '@/components/input/BaseCheckbox.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import BaseDatepicker from '@/components/input/BaseDatepicker.vue';
import DotsLoader from './components/loading/DotsLoader.vue';
import BaseCarousel from '@/components/carousel/BaseCarousel.vue';
import CarouselGallery from '@/components/carousel/CarouselGallery.vue';
import { Slide } from 'vue3-carousel';
import SpinnerLoader from '@/components/loading/SpinnerLoader.vue';

app.component('b-button', BaseButton);
app.component('b-input', BaseInput);
app.component('b-dialog', BaseDialog);
app.component('b-rating', BaseRating);
app.component('b-checkbox', BaseCheckbox);
app.component('VueDatePicker', VueDatePicker);
app.component('b-datepicker', BaseDatepicker);
app.component('dots-loader', DotsLoader);
app.component('b-carousel', BaseCarousel);
app.component('carousel-gallery', CarouselGallery);
app.component('V-Slide', Slide);
app.component('spinner-loader', SpinnerLoader);

// Method
import helper from './helpers/helper';
import formatValue from './helpers/formatValue';

app.config.globalProperties.$helper = helper;
app.config.globalProperties.$formatValue = formatValue;

// Vuetify
import vuetify from './plugins/vuetify';

app.use(vuetify);
app.use(pinia);
app.use(router);
app.mount('#app');