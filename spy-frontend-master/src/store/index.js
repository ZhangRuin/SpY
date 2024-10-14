import { createStore } from 'vuex'

export default createStore({
  state: {
    loading: {
      ChinaMap: true,
      Bar: true,
      Category: true,
      Gauge: true,
      TimeLine: true,
      WordCloud: true,
    }   
  },
  mutations: {
    loading:(state, newValue) => {
      state.loading[newValue.name]=newValue.value;
    },
    refreshLoading:(state) => {
      for(let key in state.loading){
        state.loading[key]=true;
      }
    }
  },
  actions: {
    setLoading: (ctx, value) => {
      ctx.commit('loading', value)
    },
    refreshLoading: (ctx) => {
      ctx.commit('refreshLoading')
    }
  }
})

