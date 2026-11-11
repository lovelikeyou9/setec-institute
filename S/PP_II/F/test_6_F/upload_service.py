import os
# [ os ] is used for working with the operating system from Python.
# import os → lets Python communicate with your computer's OS.

from werkzeug.utils import secure_filename
# [ werkzeug.utils ] is a utility module from Werkzeug, which Flask uses.
# Its purpose is to provide helper functions for common web tasks.

from PIL import Image
# [ PIL ] stands for Python Imaging Library. It is used to work with images in Python.


# filename = "photo.jpg"
# allowed_extensions = {"jpg", "jpeg", "png", "gif"}
def allowed_file(filename, allowed_extensions):
    return (

        '.' in filename and
        # turn JPG to jpg
        filename.rsplit('.', 1)[1].lower() in allowed_extensions
    )

# def delete_user_images(user_id, profile, upload_folder):
#     if not profile:
#         return
#
#     for kind in ("org", "thum"):
#         path = os.path.join(upload_folder, f"{user_id}_{kind}_{profile}")
#         if os.path.exists(path):
#             os.remove(path)

def save_user_image(file, user_id, upload_folder, allowed_extensions):
    """
    Save original + thumbnail.

    Example: user.png (5MB) for user id 1
      - 1_org_user.png  (original 5MB)
      - 1_thum_user.png (about 20% of original size = 1MB)
    Returns original filename to store in DB: user.png
    """
    # if no file is true
    if not file or file.filename == '':
        return None

    if not allowed_file(file.filename, allowed_extensions):
        return None

    # [ os.makedirs() ] is used to create a folder (directory) in Python.
    # [ upload_folder ] is the folder location/path where uploaded files will be saved.
    # [ exist_ok=True ] It's okay if the folder already exists.
    # Make sure the upload folder exists; create it if necessary, but don't complain if it already exists.
    os.makedirs(upload_folder, exist_ok=True)

    # [ secure_filename() ] is used to clean and make the uploaded filename safe before you save it.
    filename = secure_filename(file.filename)
    # [ os.path.splitext ] is used to split a filename into two parts:
    # name → "photo"
    # ext  → ".jpg"
    name, ext = os.path.splitext(filename)

    # 1_org_filename.png
    org_filename = f"{user_id}_org_{name}{ext}"
    # 1_thum_filename.png
    thum_filename = f"{user_id}_thum_{name}{ext}"

    # [ os.path.join() ] is used to combine folder paths and filenames into one correct file path.
    original_path = os.path.join(upload_folder, org_filename)
    thumb_path = os.path.join(upload_folder, thum_filename)

    # keep original file size (example: 5MB)
    # [ file.save() ] is used to save the uploaded file to your computer/server.
    file.save(original_path)

    # [ Image.open() ] is used to open/read an image file so Pillow (PIL) can work with it.
    image = Image.open(original_path)

    # thumbnail file size = original - 80%  (keep 20%)
    # file size ~ pixel area, so scale width/height by sqrt(0.2) ≈ 0.447
    # [ image.size ] gets the width and height of the image in pixels.
    # 1. Get original image size
    w, h = image.size
    # 2. Calculate scale
    # thumbnail will use about 44.7% of the original width and height.
    # So the thumbnail has approximately 20% of the original pixel area.
    scale = (0.20) ** 0.5
    # 3. Calculate thumbnail width
    # Using w = 2000:
    # 2000 × 0.447 = 894
    thumb_w = max(1, int(w * scale))
    thumb_h = max(1, int(h * scale))

    # [ image.copy() ] creates a separate copy of the original image.
    thumb = image.copy()
    # [ thumb.thumbnail ] is used to resize the image to make it smaller, while keeping the original
    # aspect ratio.
    # [ Image.Resampling.LANCZOS ] tells Pillow how to resize the image while keeping good quality.
    thumb.thumbnail((thumb_w, thumb_h), Image.Resampling.LANCZOS)

    # JPEG cannot save RGBA
    # [ thumb.mode] tells you what type of pixel/color data the image uses.
    # [ ext.lower() ] is used to convert the file extension to lowercase.
    # [ thumb.convert ] is used to change the image mode/color format. it changes the image to RGB mode.
    if thumb.mode in ("RGBA", "P") and ext.lower() in (".jpg", ".jpeg"):
        thumb = thumb.convert("RGB")

    # [ save_kw = {"optimize": True} ]This creates a dictionary of options/parameters
    # that will be used when saving the thumbnail.
    # creates a dictionary containing a saving option for Pillow.
    # [ save_kw["quality"] = 85 ] is used to set the JPEG quality to 85 when saving the image.
    # is used to set the image quality when saving a JPEG.
    save_kw = {"optimize": True}
    if ext.lower() in (".jpg", ".jpeg"):
        save_kw["quality"] = 85

    # [ thumb.save ] is used to save the thumbnail image as a file.
    thumb.save(thumb_path, **save_kw)

    return filename
