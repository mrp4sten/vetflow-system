import { Trash, Download, FileText, X } from 'lucide-react'
import { Button } from '@presentation/components/ui/button'
import { Badge } from '@presentation/components/ui/badge'

interface BulkActionsToolbarProps {
  selectedCount: number
  onClearSelection: () => void
  onDelete?: () => void
  onExportCSV?: () => void
  onExportPDF?: () => void
  canDelete?: boolean
}

export const BulkActionsToolbar: React.FC<BulkActionsToolbarProps> = ({
  selectedCount,
  onClearSelection,
  onDelete,
  onExportCSV,
  onExportPDF,
  canDelete = false,
}) => {
  if (selectedCount === 0) return null

  return (
    <div className="flex items-center justify-between gap-4 rounded-lg border bg-muted/50 px-4 py-3">
      <div className="flex items-center gap-3">
        <Badge variant="secondary" className="font-semibold">
          {selectedCount} {selectedCount === 1 ? 'item' : 'items'} selected
        </Badge>
        <Button
          variant="ghost"
          size="sm"
          onClick={onClearSelection}
          className="h-8"
        >
          <X className="mr-2 h-4 w-4" />
          Clear
        </Button>
      </div>

      <div className="flex items-center gap-2">
        {onExportCSV && (
          <Button
            variant="outline"
            size="sm"
            onClick={onExportCSV}
            className="h-8"
          >
            <Download className="mr-2 h-4 w-4" />
            Export CSV
          </Button>
        )}
        {onExportPDF && (
          <Button
            variant="outline"
            size="sm"
            onClick={onExportPDF}
            className="h-8"
          >
            <FileText className="mr-2 h-4 w-4" />
            Export PDF
          </Button>
        )}
        {canDelete && onDelete && (
          <Button
            variant="destructive"
            size="sm"
            onClick={onDelete}
            className="h-8"
          >
            <Trash className="mr-2 h-4 w-4" />
            Delete ({selectedCount})
          </Button>
        )}
      </div>
    </div>
  )
}
