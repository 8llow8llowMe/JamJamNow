import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { Providers, ReactQueryProvider } from '@src/redux/provider.tsx'
import App from './App.tsx'

// style
import './styles/index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ReactQueryProvider>
      <Providers>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </Providers>
    </ReactQueryProvider>
  </StrictMode>,
)
