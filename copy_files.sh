#!/bin/bash

# Define source and destination directories
SOURCE_DIR="/path/to/source"
DEST_DIR="/path/to/destination"

# Get today's date and time in the desired format
DATE=$(date +'%Y%m%d_%H%M%S')

# Function to process files recursively
process_files() {
    local current_dir=$1

    for filepath in "$current_dir"/*; do
        if [[ -d "$filepath" ]]; then
            # If it is a directory, process its contents recursively
            process_files "$filepath"
        elif [[ -f "$filepath" ]]; then
            # If it is a file, process the file
            relative_path="${filepath#$SOURCE_DIR/}"
            dest_path="$DEST_DIR/$relative_path"

            if [[ -f "$dest_path" ]]; then
                # File exists in the destination directory
                base_name=$(basename "$dest_path")
                dir_name=$(dirname "$dest_path")
                backup_file="${dir_name}/${base_name}_BKP"

                if [[ -f "$backup_file" ]]; then
                    # Backup file exists, rename it with today's date and time
                    mv "$backup_file" "${backup_file}_${DATE}"
                fi

                # Rename the existing file in the destination to _BKP
                mv "$dest_path" "$backup_file"
            fi

            # Ensure the destination directory exists
            mkdir -p "$(dirname "$dest_path")"

            # Copy the file to the destination directory
            cp "$filepath" "$dest_path"
        fi
    done
}

# Start processing from the source directory
process_files "$SOURCE_DIR"
