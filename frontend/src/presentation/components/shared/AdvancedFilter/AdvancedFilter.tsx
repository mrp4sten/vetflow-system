import { useState } from 'react'
import { Filter, Save, Trash2, X } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Input } from '@presentation/components/ui/input'
import { Label } from '@presentation/components/ui/label'
import { Badge } from '@presentation/components/ui/badge'
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@presentation/components/ui/sheet'
import { Separator } from '@presentation/components/ui/separator'

interface FilterField {
  name: string
  label: string
  type: 'text' | 'select' | 'date' | 'daterange'
  options?: { label: string; value: string }[]
}

interface FilterPreset {
  id: string
  name: string
  filters: Record<string, any>
}

interface AdvancedFilterProps {
  fields: FilterField[]
  onApplyFilters: (filters: Record<string, any>) => void
  activeFilters?: Record<string, any>
  presets?: FilterPreset[]
  onSavePreset?: (preset: FilterPreset) => void
  onDeletePreset?: (presetId: string) => void
}

export const AdvancedFilter: React.FC<AdvancedFilterProps> = ({
  fields,
  onApplyFilters,
  activeFilters = {},
  presets = [],
  onSavePreset,
  onDeletePreset,
}) => {
  const [filters, setFilters] = useState<Record<string, any>>(activeFilters)
  const [isOpen, setIsOpen] = useState(false)
  const [presetName, setPresetName] = useState('')
  const [showSavePreset, setShowSavePreset] = useState(false)

  const handleFilterChange = (name: string, value: any) => {
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleApply = () => {
    onApplyFilters(filters)
    setIsOpen(false)
  }

  const handleClear = () => {
    setFilters({})
    onApplyFilters({})
  }

  const handleSavePreset = () => {
    if (presetName && onSavePreset) {
      const preset: FilterPreset = {
        id: Date.now().toString(),
        name: presetName,
        filters,
      }
      onSavePreset(preset)
      setPresetName('')
      setShowSavePreset(false)
    }
  }

  const handleLoadPreset = (preset: FilterPreset) => {
    setFilters(preset.filters)
    onApplyFilters(preset.filters)
    setIsOpen(false)
  }

  const activeFilterCount = Object.keys(activeFilters).filter(
    (key) => activeFilters[key] !== undefined && activeFilters[key] !== ''
  ).length

  return (
    <Sheet open={isOpen} onOpenChange={setIsOpen}>
      <SheetTrigger asChild>
        <Button variant="outline" className="relative">
          <Filter className="mr-2 h-4 w-4" />
          Filters
          {activeFilterCount > 0 && (
            <Badge className="ml-2 h-5 w-5 rounded-full p-0 flex items-center justify-center">
              {activeFilterCount}
            </Badge>
          )}
        </Button>
      </SheetTrigger>
      <SheetContent className="w-full sm:max-w-md overflow-y-auto">
        <SheetHeader>
          <SheetTitle>Advanced Filters</SheetTitle>
          <SheetDescription>
            Apply filters to narrow down your results
          </SheetDescription>
        </SheetHeader>

        <div className="space-y-6 py-6">
          {/* Saved Presets */}
          {presets.length > 0 && (
            <>
              <div>
                <Label className="text-sm font-medium">Saved Presets</Label>
                <div className="mt-2 space-y-2">
                  {presets.map((preset) => (
                    <div
                      key={preset.id}
                      className="flex items-center justify-between p-2 border rounded-lg"
                    >
                      <Button
                        variant="ghost"
                        className="flex-1 justify-start"
                        onClick={() => handleLoadPreset(preset)}
                      >
                        {preset.name}
                      </Button>
                      {onDeletePreset && (
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => onDeletePreset(preset.id)}
                        >
                          <Trash2 className="h-4 w-4 text-destructive" />
                        </Button>
                      )}
                    </div>
                  ))}
                </div>
              </div>
              <Separator />
            </>
          )}

          {/* Filter Fields */}
          {fields.map((field) => (
            <div key={field.name} className="space-y-2">
              <Label htmlFor={field.name}>{field.label}</Label>
              {field.type === 'text' && (
                <Input
                  id={field.name}
                  value={filters[field.name] || ''}
                  onChange={(e) => handleFilterChange(field.name, e.target.value)}
                  placeholder={`Enter ${field.label.toLowerCase()}`}
                />
              )}
              {field.type === 'select' && field.options && (
                <select
                  id={field.name}
                  value={filters[field.name] || ''}
                  onChange={(e) => handleFilterChange(field.name, e.target.value)}
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                >
                  <option value="">All</option>
                  {field.options.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              )}
              {field.type === 'date' && (
                <Input
                  id={field.name}
                  type="date"
                  value={filters[field.name] || ''}
                  onChange={(e) => handleFilterChange(field.name, e.target.value)}
                />
              )}
            </div>
          ))}

          {/* Save as Preset */}
          {onSavePreset && (
            <>
              <Separator />
              {showSavePreset ? (
                <div className="space-y-2">
                  <Label htmlFor="preset-name">Preset Name</Label>
                  <div className="flex gap-2">
                    <Input
                      id="preset-name"
                      value={presetName}
                      onChange={(e) => setPresetName(e.target.value)}
                      placeholder="Enter preset name"
                    />
                    <Button onClick={handleSavePreset} size="sm">
                      <Save className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => {
                        setShowSavePreset(false)
                        setPresetName('')
                      }}
                    >
                      <X className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              ) : (
                <Button
                  variant="outline"
                  className="w-full"
                  onClick={() => setShowSavePreset(true)}
                  disabled={Object.keys(filters).length === 0}
                >
                  <Save className="mr-2 h-4 w-4" />
                  Save as Preset
                </Button>
              )}
            </>
          )}
        </div>

        {/* Actions */}
        <div className="flex gap-2 pt-4 border-t">
          <Button variant="outline" className="flex-1" onClick={handleClear}>
            Clear All
          </Button>
          <Button className="flex-1" onClick={handleApply}>
            Apply Filters
          </Button>
        </div>
      </SheetContent>
    </Sheet>
  )
}
