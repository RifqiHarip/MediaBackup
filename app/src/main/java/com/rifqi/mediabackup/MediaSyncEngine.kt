package com.rifqi.mediabackup
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaSyncEngine(private val context: Context) {

    suspend fun syncMedia(
        sourceUri: Uri,
        destUri: Uri,
        startTimestamp: Long,
        endTimestamp: Long,
        onProgress: (copied: Int, total: Int, currentFile: String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val sourceDir = DocumentFile.fromTreeUri(context, sourceUri) ?: return@withContext
        val destDir = DocumentFile.fromTreeUri(context, destUri) ?: return@withContext

        val filesToCopy = mutableListOf<DocumentFile>()
        val allFiles = sourceDir.listFiles()

        // Filter by extension and date range
        for (file in allFiles) {
            val name = file.name ?: continue
            if (file.isFile && isMedia(name)) {
                val ts = MediaDateParser.parseTimestamp(name, file.lastModified())
                if (ts in startTimestamp..endTimestamp) {
                    filesToCopy.add(file)
                }
            }
        }

        val total = filesToCopy.size
        var copied = 0

        for (file in filesToCopy) {
            val fileName = file.name ?: continue
            onProgress(copied, total, fileName)

            // Skip if file already exists in destination
            if (destDir.findFile(fileName) == null) {
                val newFile = destDir.createFile(file.type ?: "application/octet-stream", fileName)
                if (newFile != null) {
                    copyStream(file.uri, newFile.uri)
                }
            }
            copied++
        }
        onProgress(copied, total, "Done")
    }

    private fun isMedia(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".mp4") ||
                lower.endsWith(".mkv") || lower.endsWith(".mov")
    }

    private fun copyStream(src: Uri, dst: Uri) {
        context.contentResolver.openInputStream(src)?.use { input ->
            context.contentResolver.openOutputStream(dst)?.use { output ->
                input.copyTo(output, bufferSize = 8192)
            }
        }
    }
}