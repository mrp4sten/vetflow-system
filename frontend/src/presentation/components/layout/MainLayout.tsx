import { Outlet } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import { Header } from './Header'
import { useState, useEffect } from 'react'
import { CommandPalette } from '@presentation/components/shared/CommandPalette/CommandPalette'

export const MainLayout: React.FC = () => {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [commandPaletteOpen, setCommandPaletteOpen] = useState(false)

  // Keyboard shortcut for command palette (Cmd+K or Ctrl+K)
  useEffect(() => {
    const down = (e: KeyboardEvent) => {
      if (e.key === 'k' && (e.metaKey || e.ctrlKey)) {
        e.preventDefault()
        setCommandPaletteOpen((open) => !open)
      }
    }

    document.addEventListener('keydown', down)
    return () => document.removeEventListener('keydown', down)
  }, [])

  return (
    <div className="min-h-screen bg-background">
      <Header 
        onMenuToggle={() => setSidebarCollapsed(!sidebarCollapsed)} 
        sidebarCollapsed={sidebarCollapsed}
        onSearchClick={() => setCommandPaletteOpen(true)}
      />
      <div className="flex">
        <Sidebar collapsed={sidebarCollapsed} />
        <main className={`flex-1 transition-all duration-300 ${
          sidebarCollapsed ? 'ml-16' : 'ml-64'
        }`}>
          <div className="p-6">
            <Outlet />
          </div>
        </main>
      </div>
      
      {/* Command Palette */}
      <CommandPalette 
        open={commandPaletteOpen} 
        onOpenChange={setCommandPaletteOpen} 
      />
    </div>
  )
}